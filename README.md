# Hubitat Shinobi

An integration of the Shinobi video NVR with Hubitat home automation. The integration allows Hubitat to see motion events from any of your Shinobi cameras and also allows Hubitat to control montor modes - Stop, Watch, and Record, trigger a motion event on a Monitor, and send PTZ controls to monitors. 

![Shinobi Logo](https://shinobi.video/libs/assets/icon/apple-touch-icon-152x152.png)  ![Hubitat Logo](https://cdn.shopify.com/s/files/1/2575/8806/t/20/assets/hubitat-logo-white.png)

## Installation Instructions
1. Install and configure [Shinobi](https://shinobi.video/)          

2. Install and configure the [Hubitat Package Manager](https://github.com/dcmeglio/hubitat-packagemanager) and use it to install this integration.

## Setup and Use

1. Open Apps and click "Add USer App"

2. Select the Shinobi App from the list

3. Under the Install/Setup section click "Tap to Setup..."

4. Within Shinobi, create a new API key. This is under the main Menu-->API and click add. This key needs at least "Can Get Monitors" and "Can Control Monitors" access to use all the features in this integration. 

5. Get the group key for your user in Shinobi. This is in the main menu-->Settings under the Account Info section.

6. Enter tghe Group key and API Key in the correct locations in Hubitat

7. Click Next

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




