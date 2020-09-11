/**
 *  Copyright 2020 Brad Sileo
 *
 *  Shinobi NVR
 *
 *  Author: Brad Sileo
 *
 *
 *  version: 0.9.1
 */
metadata {
	definition (name: "Shinobi Monitor", namespace: "bsileo", author: "Brad Sileo")
    {
	    capability "Switch"
        capability "MotionSensor"
        capability "Refresh"
        attribute "mode", "string"
        attribute "status", "string"
        command"up"
        command "down"
        command "left"
        command "right"
        command "zoomIn"
        command "zoomOut"
        command "enableNV"
        command "disableNV"
        command "trigger", [
            [name: "region", type: "STRING", description: "The name of the region. Example : door"],
            [name: "reason", type: "STRING", description: "The reason for this trigger. Example : motion"],
            [name: "confidence", type: "NUMBER", description: "A number to signify how much confidence this engine has that there is motion. Example : 197.4755859375"],
            ]
        command "start", [
            [name:"units*",
                     type: "ENUM",
                     description: "The units for the time or select No Timer to Watch until stopped",
                     constraints: [
                         "0": "no timer",
                         "min" : "minutes",
                         "hr": "hours",
                         "day": "days"
     	             ]
                ],
            [name:"value",
                     type: "NUMBER",
                     description: "The amount of time to stay in Watch mode before stopping",
                ]
            ]
        command "stop"
        command "record", [
                [name:"units*",
                     type: "ENUM",
                     description: "The units for the time or select No Timer to Record until stopped",
                     constraints: [
                         "0": "no timer",
                         "min" : "minutes",
                         "hr": "hours",
                         "day": "days"
     	             ]
                ],
            [name:"value",
                     type: "NUMBER",
                     description: "The amount of time to Record for",
                ]
        ]
	}

    preferences {
         section("General:") {
              input ( name: "motionTimeout",
        	    title: "Timeout until motion is considered stopped, measured in seconds",
        	    type: "number",
        	    defaultValue: "30"
                )
             input ( name: "enableMotionTimeout",
        	    title: "Enable automatic motion timeout",
        	    type: "bool",
        	    defaultValue: true
                )
            input (
        	name: "loggingLevel",
        	title: "IDE Live Logging Level:\nMessages with this level and higher will be logged to the IDE.",
        	type: "enum",
        	options: [
        	    "None",
        	    "Error",
        	    "Warning",
        	    "Info",
        	    "Debug",
        	    "Trace"
        	],
        	defaultValue: "Info",
            displayDuringSetup: true,
        	required: false
            )
        }
    }
}

def installed() {
    state.loggingLevel = (settings.loggingLevel) ? settings.loggingLevel : 'Info'
    getHubPlatform()
}

def updated() {
    state.loggingLevel = (settings.loggingLevel) ? settings.loggingLevel : 'Info'
}

def refresh() {
    sendMonitorCommand() { resp ->
        parseRefresh(resp.data)
    }
}

def parseRefresh(monitor) {
    logger("Parse Shinobi - ${monitor}","debug")
    sendEvent(name: "status", value: monitor.status)
    sendEvent(name: "mode", value: monitor.mode)
}




def triggerMotion(event) {
    logger("Trigger Motion","info")
    logger("Trigger Motion - ${event}","debug")
    def desc = "Motion detected"
    if (event.region) {
        sendEvent([[name: "motionRegion", value: event.region]])
        desc = desc + " in ${event.region}"
    }
    sendEvent(name: "motion", value: "active", descriptionText: desc)
    if (settings.enableMotionTimeout) {
        logger("Schedule noMotion for ${settings.motionTimeout} seconds","info")
        unschedule()
        runIn(settings.motionTimeout, noMotion)
    }
}

def triggerNoMotion(event) {
    logger("Trigger NO Motion - ${event}","debug")
    sendEvent([[name: "motion", value: "inactive"]])
}

def noMotion() {
    logger("Auto NO Motion","info")
    sendEvent([[name: "motion", value: "inactive"]])
}

def timeInterval(time, unit) {
    state.nextTime = time
    state.nextUnit = unit
}

def on() {
     record()
}

def off() {
     stop()
}

def record(units="no timer", time=null) {
    sendMonitorCommand("record",units, time) { resp ->
        logger("Record result->${resp.data}","info")
        refresh()
    }
}

def stop() {
    sendMonitorCommand("stop",null, null) { resp ->
        logger("Stop result->${resp.data}","info")
        refresh()
    }
}

def start(units="no timer", time=null) {
    sendMonitorCommand("start",units, time) { resp ->
        logger("Start result->${resp.data}","info")
        refresh()
    }
}

def center() {
     sendControlCommand("center")
}

def up() {
     sendControlCommand("up")
}

def down() {
     sendControlCommand("down")
}

def left() {
     sendControlCommand("left")
}

def right() {
     sendControlCommand("right") { resp ->
        logger("Right result->${resp.data}","info")
    }
}

def zoomIn() {
     sendControlCommand("zoom_in")
}

def zoomOut() {
     sendControlCommand("zoom_out")
}

def enableNV() {
     sendControlCommand("enable_nv")
}

def disableNV() {
     sendControlCommand("disable_nv")
}

def trigger(region="",reason="motion", confidence=197) {
     sendMotionCommand(device.deviceNetworkId,region,reason,confidence) { resp ->
        logger("Trigger result->${resp.data}","info")
    }
}

def sendMonitorCommand(command=null, units="no timer", time=null, closure) {
     sendCommand("monitor",command, units, time, null, closure)
}

def sendControlCommand(command, closure) {
     sendCommand("control", command, null, null, null, closure)
}

def sendMotionCommand(plug, region, reason, confidence, closure) {
    /* From https://shinobi.video/docs/api#content-trigger-a-motion-event
             plug : The name of the plugin. You can put the name of the camera.
             name : The name of the region. Example : door
             reason : The reason for this trigger. Example : motion
             confidence : A number to signify how much confidence this engine has that there is motion. Example : 197.4755859375
    */
    def query = "data={\"plug\":\"${plug}\",\"name\":\"${region}\",\"reason\":\"${reason}\",\"confidence\":\"${confidence}\"}"
    sendCommand("motion",null, null, null, query, closure)
}


private sendCommand(type, command=null, units="no timer", time=null, query=null, closure) {
    logger("Start SMC of ${type} with ${units} for ${time} - ${command}","debug")
    def controller = getParent().getControllerParams()
    def body = ""
    def path = "/${controller.APIKey}/${type}/${controller.groupKey}/${device.deviceNetworkId}"
    if (command) { path = path + "/$command" }
    if (units != null && units != "no timer") {
        path = path + "/${time}/${units}"
    }
    if (query) {
        path = path + "?${query}"
    }

    def params = [
        uri: controller.uri,
        path: path,
        requestContentType: "application/json",
        contentType: "application/json",
        body:body
    ]
    logger("Run command with ${params}","debug")
    logger("URL = ${params.uri}${params.path}","debug")
    httpGet(params) { resp ->
        logger("SMC result->${resp.data}","debug")
        closure(resp)
    }
}

// INTERNAL Methods

//*******************************************************
//*  logger()
//*
//*  Wrapper function for all logging.
//*******************************************************

private logger(msg, level = "debug") {

    def lookup = [
        	    "None" : 0,
        	    "Error" : 1,
        	    "Warning" : 2,
        	    "Info" : 3,
        	    "Debug" : 4,
        	    "Trace" : 5]
      def logLevel = lookup[state.loggingLevel ? state.loggingLevel : 'Debug']
     // log.debug("Lookup is now ${logLevel} for ${state.loggingLevel}")

    switch(level) {
        case "error":
            if (logLevel >= 1) log.error msg
            break

        case "warn":
            if (logLevel >= 2) log.warn msg
            break

        case "info":
            if (logLevel >= 3) log.info msg
            break

        case "debug":
            if (logLevel >= 4) log.debug msg
            break

        case "trace":
            if (logLevel >= 5) log.trace msg
            break

        default:
            log.debug msg
            break
    }
}

// **************************************************************************************************************************
// SmartThings/Hubitat Portability Library (SHPL)
// Copyright (c) 2019, Barry A. Burke (storageanarchy@gmail.com)
//
// The following 3 calls are safe to use anywhere within a Device Handler or Application
//  - these can be called (e.g., if (getPlatform() == 'SmartThings'), or referenced (i.e., if (platform == 'Hubitat') )
//  - performance of the non-native platform is horrendous, so it is best to use these only in the metadata{} section of a
//    Device Handler or Application
//
private String  getPlatform() { (physicalgraph?.device?.HubAction ? 'SmartThings' : 'Hubitat') }	// if (platform == 'SmartThings') ...
private Boolean getIsST()     { (physicalgraph?.device?.HubAction ? true : false) }					// if (isST) ...
private Boolean getIsHE()     { (hubitat?.device?.HubAction ? true : false) }						// if (isHE) ...
//
// The following 3 calls are ONLY for use within the Device Handler or Application runtime
//  - they will throw an error at compile time if used within metadata, usually complaining that "state" is not defined
//  - getHubPlatform() ***MUST*** be called from the installed() method, then use "state.hubPlatform" elsewhere
//  - "if (state.isST)" is more efficient than "if (isSTHub)"
//
private String getHubPlatform() {
    if (state?.hubPlatform == null) {
        state.hubPlatform = getPlatform()						// if (hubPlatform == 'Hubitat') ... or if (state.hubPlatform == 'SmartThings')...
        state.isST = state.hubPlatform.startsWith('S')			// if (state.isST) ...
        state.isHE = state.hubPlatform.startsWith('H')			// if (state.isHE) ...
    }
    return state.hubPlatform
}
private Boolean getIsSTHub() { (state.isST) }					// if (isSTHub) ...
private Boolean getIsHEHub() { (state.isHE) }