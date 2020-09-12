# Hubitat Shinobi

An integration of the Shinobi video NVR with Hubitat home automation. The integration allows Hubitat to see motion events from any of your Shinobi cameras and also allows Hubitat to control montor modes - Stop, Watch, and Record, trigger a motion event on a Monitor, and send PTZ controls to monitors.

![Shinobi Logo](https://shinobi.video/libs/assets/icon/apple-touch-icon-152x152.png)  ![Hubitat Logo](https://cdn.shopify.com/s/files/1/2575/8806/t/20/assets/hubitat-logo-white.png)

# What can I do with this integration?

The API of Shinobi is connected to Hubitat so that all motion detection activity in any Shinobi Monitor can be used as a motion detector in Hubitat. other data, such as tyhe reegion for a motion detection event, is also available as a custom event in Hubitat and could be consumed in the the Rule Machine or other Apps to drive behavior. We also provide access to the Monitor APIs in Shinobi so that a motion event can be trigger on a camera from data in Hubitat, which may automatically start recording, for example. A camera can also be trigger to start watching or recording for a time interval or indiefinitely. Finally for cameras that support PTZ behavior via Shinobi, these API endpoints are exposed as commands in Hubitat so they can be called from other Apps, rules, etc.

The basic installation will create a motion sensor for each Monitor in Shinobi.  Once you have configured the Webhook these motion sensros will reflect any motion detected in this Monitor. You can tune the motion sensor using Shinobi settings, including using different plugins, etc. to drive this behavior. If you use the Region editor in Shinobi, then the region information is also passed into Hubitat. You can use this as a separate event on the main Montior device, but you can also create additional motion sensors for each region. To do this, after you define your regions in Shinobi, go to the Monitor object in Hubitat and use the "Add Region Child" action to create a new region with the same name as that assigned in Shinobi. This new motion sensor will now be triggered active only when Shinobi detects motion in that region. 


## Installation Instructions
1. Install and configure [Shinobi](https://shinobi.video/)

2. Install and configure the [Hubitat Package Manager](https://github.com/dcmeglio/hubitat-packagemanager) and use it to install this integration.

## Setup and Use

1. Within Shinobi, create a new API key. This is under the main Menu-->API and click Add. This key needs at least "Can Get Monitors" and "Can Control Monitors" access to use all the features in this integration.

2. Open Apps and click "Add User App"

3. Select the Shinobi App from the list

4. Under the Install/Setup Section click "Tap to Setup..."

5. Get the group key for your user in Shinobi. This is in the main menu-->Settings under the Account Info section.

6. Enter the Group key and API Key in the correct locations in Hubitat

7. Click Next...

8. Select the monitors (cameras) to install into Hubitat. A new device will be created for each.

9. Setup Motion events from Montitors in Shinobi to Hubitat:

*  Open the Shinobi App in Hubitat to find the prebuilt URLs.
*  Copy and paste these URLS into the settings for any monitor you want to connect to Hubitat.
* Under Global detector Settings:
  - Webhook: Yes
  - Webhook URL: [pasted from Hubitat]
* Under No Motion Detector
  - Webhook: Yes
  - Webhook URL: [pasted from Hubitat No Motion URL]




