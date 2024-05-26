
# LynxVR

> [!WARNING]  
> The current release does not include backend code to access heartrate service!

LynxVR is a handy app that bridges the gap between your Wear OS smartwatch and your VR adventures. It lets you send your real-time heart rate (HR) data directly into VR games like Resonite and VR Chat, adding a new layer of immersion and personalization.

- Real-time Heart Rate Tracking: See your heart rate reflected in-game, adding a layer of realism that reflects your exertion during gameplay.
- VR Game Compatibility: Works seamlessly with popular VR titles like Resonant and VR Chat, allowing you to showcase your physical reactions to others.
- OBS Integration: Stream your VR gameplay with your heart rate data overlaid on top, providing viewers with a more engaging and informative experience.
- Standalone Mode: No app required for sending heart rate data via OSC in VRChat on Quest devices or PC.

# Download
Releasing Soon - July 2024

<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Google_Play_Store_badge_EN.svg/2560px-Google_Play_Store_badge_EN.svg.png" height="50">

# Supported HR Devices

> [!Tip]  
> LynxVR works best with Pixel Watches due to their constant heartrate tracking!

| Device Name           | Operating System | Standalone Mode | LynxVR PC App | Known Issues |
|-----------------------|------------------|-----------------|---------------| ------------- |
| Pixel Watch 2          | WearOS 4         | ✅               | ✅             | |
| Pixel Watch            | WearOS 4         | ✅               | ✅             | |
| Samsung Galaxy Watch   | WearOS 4         | ✅               | ✅             | Sends in brusts instead of constant HR. |

[See more devices](https://github.com/lynixfur/LynxVR/wiki/Device-Compatibility-List)

# API Specification

### VRChat 
VRChat utilizes OSC to receive data directly from the watch.

``/avatar/parameters/lynxvr_hr`` is used for receiving BPM as a integer value. <br>
``/avatar/parameters/lynxvr_batt`` is used for receiving battery health of the device.

### Resonite

Resonite uses Websockets to connect to the watch via a companion app (LynxVR Desktop) or cloud variables with api.lynix.ca 

``heartrate,battery_percentage`` is the values expected as integers. <br>
``76,100`` is an example of how csv data is passed to resonite.

# Credit 
Gawdl3y - Some inspiration for this app is based on [Heartsock](https://github.com/Gawdl3y/heartsock-app) 

# Contributions
Want to contribute? Share your ideas and create a PR! We're always looking for some help!
