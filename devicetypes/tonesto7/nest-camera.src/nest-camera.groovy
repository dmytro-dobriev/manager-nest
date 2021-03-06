/**
 *  Nest Cam
 *	Authors: Anthony S. (@tonesto7)
 *	Contributors: Ben W. (@desertblade), Eric S. (@E_Sch)
 *  A Huge thanks goes out to Greg (@ghesp) for all of your help getting this working.
 *
 *	Copyright (C) 2017, 2018, 2019 Anthony S.
 * 	Licensing Info: Located at https://raw.githubusercontent.com/tonesto7/nest-manager/master/LICENSE.md
 */

import java.text.SimpleDateFormat
import groovy.time.TimeCategory

preferences { }

def devVer() { return "5.4.5" }

metadata {
	definition (name: "${textDevName()}", author: "Anthony S.", namespace: "tonesto7") {
		capability "Actuator"
		capability "Sensor"
		capability "Switch"
		capability "Motion Sensor"
		capability "Sound Sensor"
		capability "Refresh"
		capability "Image Capture"
		capability "Video Camera"
		//capability "Video Capture"
		capability "Health Check"

		command "refresh"
		command "poll"
		command "log", ["string","string"]
		command "streamingOn"
		command "streamingOff"
		command "chgStreaming"
		command "cltLiveStreamStart"

		attribute "devVer", "string"
		attribute "softwareVer", "string"
		attribute "lastConnection", "string"
		attribute "lastOnlineChange", "string"
		attribute "lastUpdateDt", "string"
		attribute "activityZoneName", "string"
		attribute "isStreaming", "string"
		attribute "audioInputEnabled", "string"
		attribute "nestAware", "string"
		attribute "motionPerson", "string"
		attribute "videoQualityLevel", "string"
		attribute "publicShareEnabled", "string"
		attribute "lastEventStart", "string"
		attribute "lastEventEnd", "string"
		attribute "lastEventType", "string"
		attribute "lastEventZones", "string"
		attribute "apiStatus", "string"
		attribute "debugOn", "string"
		attribute "devTypeVer", "string"
		attribute "onlineStatus", "string"
		attribute "securityState", "string"
	}

	simulator { }

	tiles(scale: 2) {
		multiAttributeTile(name: "videoPlayer", type: "videoPlayer", width: 6, height: 4) {
			tileAttribute("device.switch", key: "CAMERA_STATUS") {
				attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", action: "switch.off", backgroundColor: "#00A0DC", defaultState: true)
				attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", action: "switch.on", backgroundColor: "#ffffff")
				attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#00A0DC")
				attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", action: "refresh.refresh", backgroundColor: "#cccccc")
			}
			tileAttribute("device.errorMessage", key: "CAMERA_ERROR_MESSAGE") {
				attributeState("errorMessage", label: "", value: "", defaultState: true)
			}
			tileAttribute("device.camera", key: "PRIMARY_CONTROL") {
				attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", backgroundColor: "#00A0DC", defaultState: true)
				attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", backgroundColor: "#ffffff")
				attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#00A0DC")
				attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", backgroundColor: "#cccccc")
			}
			tileAttribute("device.startLive", key: "START_LIVE") {
				attributeState("live", action: "cltLiveStreamStart", defaultState: true)
			}
			tileAttribute("device.stream", key: "STREAM_URL") {
				attributeState("activeURL", defaultState: true)
			}
		}
		standardTile("isStreamingStatus", "device.isStreaming", width: 2, height: 2, decoration: "flat") {
			state("on", label: "Streaming", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_green_icon.png", backgroundColor: "#00a0dc")
			state("off", label: "Off", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_gray_icon.png", backgroundColor: "#ffffff")
			state("updating", label:"", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
			state("offline", label: "Offline", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_red_icon.png", backgroundColor: "#cccccc")
			state("unavailable", label: "Unavailable", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_red_icon.png", backgroundColor: "#F22000")
		}
		standardTile("isStreaming", "device.isStreaming", width: 2, height: 2, decoration: "flat") {
			state("on", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_stream_btn_icon.png")
			state("off", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_off_btn_icon.png")
			state("updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
			state("offline", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_offline_btn_icon.png")
			state("unavailable", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_offline_btn_icon.png")
		}
		carouselTile("cameraDetails", "device.image", width: 6, height: 4) { }
		standardTile("take", "device.image", width: 2, height: 2, canChangeIcon: false, decoration: "flat", canChangeBackground: false) {
			state "take", action: "Image Capture.take", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_snapshot_icon.png", nextState:"taking"
			state "taking", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png"
			state "image", action: "Image Capture.take", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_snapshot_icon.png", nextState:"taking"
		}
		standardTile("motion", "device.motion", width: 2, height: 2, decoration: "flat", wordWrap: true) {
			state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#00a0dc"
			state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
		}
		standardTile("sound", "device.sound", width: 2, height: 2, decoration: "flat", wordWrap: true) {
			state "detected", label:'Noise', icon:"st.Entertainment.entertainment15", backgroundColor:"#00a0dc"
			state "not detected", label:'Quiet', icon:"st.Entertainment.entertainment15", backgroundColor:"#ffffff"
		}
		valueTile("softwareVer", "device.softwareVer", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Firmware:\nv${currentValue}')
		}
		valueTile("lastConnection", "device.lastConnection", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Camera Last Checked-In:\n${currentValue}')
		}
		valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Network Status:\n${currentValue}')
		}
		valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Data Last Received:\n${currentValue}')
		}
		valueTile("devTypeVer", "device.devTypeVer",  width: 3, height: 1, decoration: "flat") {
			state("default", label: 'Device Type:\nv${currentValue}', defaultState: true)
		}
		valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, decoration: "flat", wordWrap: true) {
                        state "Good", label: "API Status:\nOK"
                        state "Sporadic", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
                        state "Outage", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
		}
		standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/refresh_icon.png"
		}
		htmlTile(name:"devCamHtml", action: "getCamHtml", width: 6, height: 10, whitelist: ["raw.githubusercontent.com", "cdn.rawgit.com", "cdnjs.cloudflare.com"])
		valueTile("remind", "device.blah", inactiveLabel: false, width: 6, height: 2, decoration: "flat", wordWrap: true) {
			state("default", label: 'Reminder:\nHTML Content is Available in SmartApp', defaultState: true)
		}
		main "isStreamingStatus"
		details(["videoPlayer", "isStreaming", "take", "refresh", "cameraDetails", "motion", "sound","onlineStatus","debugOn",  "apiStatus",  "lastConnection", "lastUpdatedDt", "lastTested","devTypeVer",  "softwareVer", "devCamHtml", "remind" ])

	}
	preferences {
		input "enableEvtSnapShot", "bool", title: "Take Snapshot on Motion Events?", description: "", defaultValue: true, displayDuringSetup: false
		input "motionOnPersonOnly", "bool", title: "Only Trigger Motion Events When Person is Detected?", description: "", defaultValue: false, displayDuringSetup: false
	}
}

mappings {
	path("/getInHomeURL") {action: [GET: "getInHomeURL"]}
	path("/getCamHtml") {action: [GET: "getCamHtml"]}
}

def getInHomeURL() { return [InHomeURL: (getCamPlaylistURL() ?: "")] }

def initialize() {
	Logger("initialized...")
	state?.healthInRepair = false
	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 2000) {
		state.updatedLastRanAt = now()
		verifyHC()
		state?.isInstalled = true
	} else {
		log.trace "initialize(): Ran within last 2 seconds - SKIPPING"
	}
}

void installed() {
	Logger("installed...")
	runIn(5, "initialize", [overwrite: true] )
	state?.shownChgLog = true
	runIn(15, "refresh", [overwrite: true])
}

void updated() {
	Logger("updated...")
	runIn(5, "initialize", [overwrite: true] )
}

def useTrackedHealth() { return state?.useTrackedHealth ?: false }

def getHcTimeout() {
	def to = state?.hcTimeout
	return ((to instanceof Integer) ? to.toInteger() : 120)*60
}

void verifyHC() {
	if(useTrackedHealth()) {
		def timeOut = getHcTimeout()
		if(!val || val.toInteger() != timeOut) {
			Logger("verifyHC: Updating Device Health Check Interval to $timeOut")
			sendEvent(name: "checkInterval", value: timeOut, data: [protocol: "cloud"], displayed: false)
		}
	} else {
		sendEvent(name: "DeviceWatch-Enroll", value: groovy.json.JsonOutput.toJson(["protocol":"cloud", "scheme":"untracked"]), displayed: false)
	}
	repairHealthStatus(null)
}

def modifyDeviceStatus(status) {
	if(status == null) { return }
	def val = status.toString() == "offline" ? "offline" : "online"
	if(val != getHealthStatus(true)) {
		sendEvent(name: "DeviceWatch-DeviceStatus", value: val.toString(), displayed: false, isStateChange: true)
		Logger("UPDATED: DeviceStatus Event: '$val'")
	}
}

def ping() {
	Logger("ping...")
	keepAwakeEvent()
}

def keepAwakeEvent() {
	def lastDt = state?.lastUpdatedDtFmt
	if(lastDt) {
		def ldtSec = getTimeDiffSeconds(lastDt)
		//log.debug "ldtSec: $ldtSec"
		if(ldtSec < 1900) {
			poll()
		}
	}
}

void repairHealthStatus(data) {
	Logger("repairHealthStatus($data)")
	if(state?.hcRepairEnabled != false) {
		if(data?.flag) {
			sendEvent(name: "DeviceWatch-DeviceStatus", value: "online", displayed: false, isStateChange: true)
			state?.healthInRepair = false
		} else {
			state.healthInRepair = true
			sendEvent(name: "DeviceWatch-DeviceStatus", value: "offline", displayed: false, isStateChange: true)
			runIn(7, repairHealthStatus, [data: [flag: true]])
		}
	}
}

def parse(String description) {
	LogAction("Parsing '${description}'", "debug")
}

void poll() {
	Logger("polling parent...")
	parent.refresh(this)
}

void refresh() {
	state?.camApiServerData = null
	poll()
}

void cltLiveStreamStart() {
	//log.trace "video stream start()"
	String url = getCamPlaylistURL()
	String imgUrl = "http://cdn.device-icons.smartthings.com/camera/dlink-indoor@2x.png"
	if(url && imgUrl) {
		Map dataLiveVideo = [OutHomeURL: url, InHomeURL: url, ThumbnailURL: imgUrl, cookie: [key: "key", value: "value"]]
		def evtData = groovy.json.JsonOutput.toJson(dataLiveVideo)
		sendEvent(name: "stream", value: evtData.toString(), data: evtData, descriptionText: "Starting the Live Video Stream", eventType: "VIDEO", displayed: false, isStateChange: true)
	}
}

// parent calls this method to queue data.
// goal is to return to parent asap to avoid execution timeouts

def generateEvent(Map eventData) {
	//log.trace("generateEvent Parsing data ${eventData}")
	state.eventData = eventData
	//runIn(1, "processEvent", [overwrite: true] )
	processEvent()
}

def processEvent() {
	if(state?.swVersion != devVer()) {
		initialize()
		state.swVersion = devVer()
		state.shownChgLog = false
		state.androidDisclaimerShown = false
	}
	def eventData = state?.eventData
	state.eventData = null
	def dtNow = getDtNow()
	//log.trace("processEvent Parsing data ${eventData}")
//	try {
		LogAction("------------START OF API RESULTS DATA------------", "warn")
		if(eventData) {
			def results = eventData?.data
			// log.debug "results: $results"
			state.isBeta = eventData?.isBeta == true ? true : false
			state.hcRepairEnabled = eventData?.hcRepairEnabled == true ? true : false
			state.takeSnapOnEvt = eventData?.camTakeSnapOnEvt == true ? true : false
			state.restStreaming = eventData?.restStreaming == true ? true : false
			state.showLogNamePrefix = eventData?.logPrefix == true ? true : false
			state.enRemDiagLogging = eventData?.enRemDiagLogging == true ? true : false
			state.streamMsg = eventData?.streamNotify == true ? true : false
			state.healthMsg = eventData?.healthNotify?.healthMsg == true ? true : false
			state.healthMsgWait = eventData?.healthNotify?.healthMsgWait
			state.motionSndChgWaitVal = eventData?.motionSndChgWaitVal ? eventData?.motionSndChgWaitVal?.toInteger() : 60
			if(eventData?.hcTimeout && (state?.hcTimeout != eventData?.hcTimeout || !state?.hcTimeout)) {
				state.hcTimeout = eventData?.hcTimeout
				verifyHC()
			}
			state.useMilitaryTime = eventData?.mt ? true : false
			state.clientBl = eventData?.clientBl == true ? true : false
			state.mobileClientType = eventData?.mobileClientType
			state.nestTimeZone = eventData?.tz ?: null

			state?.devBannerData = eventData?.devBannerData ?: null
			onlineStatusEvent(results?.is_online?.toString())
			isStreamingEvent(results?.is_streaming)
			publicShareUrlEvent(results?.public_share_url)
			securityStateEvent(eventData?.secState)
			publicShareEnabledEvent(results?.is_public_share_enabled?.toString())
			videoHistEnabledEvent(results?.is_video_history_enabled?.toString())
			if(results?.last_is_online_change) { lastOnlineEvent(results?.last_is_online_change?.toString()) }
			if(eventData?.allowDbException) { state?.allowDbException = eventData?.allowDbException == false ? false : true }
			apiStatusEvent(eventData?.apiIssues)
			debugOnEvent(eventData?.debug ? true : false)
			state?.camMotionZones = eventData?.camMotionZones ?: []
			audioInputEnabledEvent(results?.is_audio_input_enabled?.toString())
			softwareVerEvent(results?.software_version?.toString())
			if(results?.activity_zones) { state?.activityZones = results?.activity_zones }

			//if(results?.snapshot_url) { state?.snapshot_url = results?.snapshot_url?.toString() }
			imageEvent(results?.snapshot_url?.toString())
			if(results?.app_url) { state?.app_url = results?.app_url?.toString() }
			if(results?.web_url) { state?.web_url = results?.web_url?.toString() }
			if(results?.last_event) {
				// state?.animation_url = null
				if(results?.last_event?.animated_image_url) { state?.animation_url = results?.last_event?.animated_image_url }
				if(results?.last_event.start_time && results?.last_event.end_time) { lastEventDataEvent(results?.last_event) }
			}
			deviceVerEvent(eventData?.latestVer?.toString())
			// videoQualityEvent()
			// findCameraModel()
			lastUpdatedEvent(true)
			checkHealth()
			if(state?.ok2Checkin == true) {
				lastCheckinEvent(dtNow)
				//log.debug "lastCheckin Reason's: ${state?.ok2CheckinRes}"
			}
			// Logger("Device Health Status: ${device.getStatus()}")
		}
		return null
/*	}
	catch (ex) {
		def s = ""
		if(ex && ex?.message) { s = ex?.message?.toString() }
		log.error "processEvent Exception: ${s}", ex
		exceptionDataHandler(s, "processEvent")
	}
*/
}

def getStateSize()      { return state?.toString().length() }
def getStateSizePerc()  { return (int) ((stateSize/100000)*100).toDouble().round(0) }
def getDevTypeId() { return device?.getTypeId() }

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def getDeviceStateData() {
	return getState()
}

def evtSnapShotOk() {
	if(!state?.isOnline || !state?.isStreaming || !state?.takeSnapOnEvt) { return false }
	return settings?.enableEvtSnapShot == false ? false : true
}

def addCheckinReason(str) {
	if(state?.ok2Checkin != true) {
		state?.ok2CheckinRes = []
		state?.ok2Checkin = true
	}
	def res = state?.ok2CheckinRes ?: []
	res.push(str?.toString())
	state?.ok2CheckinRes = res
}

def getTimeZone() {
	def tz = null
	if (location?.timeZone) { tz = location?.timeZone }
	else { tz = state?.nestTimeZone ? TimeZone.getTimeZone(state?.nestTimeZone) : null }
	if(!tz) { Logger("getTimeZone: Hub or Nest TimeZone is not found ...", "warn") }
	return tz
}

def isCodeUpdateAvailable(newVer, curVer) {
	def result = false
	def latestVer
	def versions = [newVer, curVer]
	if(newVer != curVer) {
		latestVer = versions?.max { a, b ->
			def verA = a?.tokenize('.')
			def verB = b?.tokenize('.')
			def commonIndices = Math.min(verA?.size(), verB?.size())
			for (int i = 0; i < commonIndices; ++i) {
				//log.debug "comparing $numA and $numB"
				if (verA[i]?.toInteger() != verB[i]?.toInteger()) {
					return verA[i]?.toInteger() <=> verB[i]?.toInteger()
				}
			}
			verA?.size() <=> verB?.size()
		}
		result = (latestVer == newVer) ? true : false
	}
	//log.debug "type: $type | newVer: $newVer | curVer: $curVer | newestVersion: ${latestVer} | result: $result"
	return result
}

def deviceVerEvent(ver) {
	def curData = device.currentState("devTypeVer")?.value?.toString()
	def pubVer = ver ?: null
	def dVer = devVer() ?: null
	def newData = isCodeUpdateAvailable(pubVer, dVer) ? "${dVer}(New: v${pubVer})" : "${dVer}" as String
	state?.devTypeVer = newData
	state?.updateAvailable = isCodeUpdateAvailable(pubVer, dVer)
	if(isStateChange(device, "devVer", dVer?.toString())) {
		sendEvent(name: 'devVer', value: dVer, displayed: false)
	}
	if(isStateChange(device, "devTypeVer", newData?.toString())) {
		Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
		sendEvent(name: 'devTypeVer', value: newData, displayed: false)
		addCheckinReason("devTypeVer")
	} else { LogAction("Device Type Version is: (${newData}) | Original State: (${curData})") }
}

def lastCheckinEvent(checkin) {
	def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
	tf.setTimeZone(getTimeZone())
	def lastConn = checkin ? tf?.format(Date.parse("E MMM dd HH:mm:ss z yyyy", checkin?.toString())) : "Not Available"
	def lastChk = device.currentState("lastConnection")?.value
	state?.lastConnection = lastConn?.toString()
	if(isStateChange(device, "lastConnection", lastConn?.toString())) {
		LogAction("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
		sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: false)
		state?.ok2Checkin = false
	} else { LogAction("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
}

def lastOnlineEvent(dt) {
	def lastOnlVal = device.currentState("lastOnlineChange")?.value
	def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
	tf.setTimeZone(getTimeZone())
	def lastOnl = !dt ? "Nothing To Show..." : tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dt.toString()))
	state?.lastOnl = lastOnl
	if(isStateChange(device, "lastOnlineChange", lastOnl?.toString())) {
		Logger("UPDATED | Last Online Change was: (${lastOnl}) | Original State: (${lastOnlVal})")
		sendEvent(name: 'lastOnlineChange', value: lastOnl, displayed: false, isStateChange: true)
		addCheckinReason("lastOnlineChange")
	} else { LogAction("Last Online Change was: (${lastOnl}) | Original State: (${lastOnlVal})") }
}

def onlineStatusEvent(isOnline) {
	LogAction("onlineStatusEvent($isOnline)")
	// if(state?.camApiServerData) {
	// 	state?.camApiServerData?.items[0]?.capabilities?.each {
	// 		if(it?.startsWith("streaming.cameraprofile") || it?.startsWith("streaming.data-usage-tier")) {
	// 			log.debug "${it}"
	// 		}
	// 	}
	// }
	if(state?.camApiServerData && (state?.camApiServerData?.items[0]?.is_online != isOnline?.toBoolean() ) ) {
		Logger("onlineStatusEvent: ${isOnline?.toBoolean()} | CamData: ${state?.camApiServerData?.items[0]?.is_online}")
		//isOnline = state?.camApiServerData?.items[0]?.is_online
		state.camApiServerData = null
	}
	def onlineStat = isOnline?.toString() == "true" ? "online" : "offline"
	state?.isOnline = (onlineStat == "online")
	modifyDeviceStatus(onlineStat)
	def prevOnlineStat = device.currentState("onlineStatus")?.value
	state?.onlineStatus = onlineStat?.toString().capitalize()
	if(isStateChange(device, "onlineStatus", onlineStat?.toString())) {
		Logger("UPDATED | Online Status is: (${onlineStat}) | Original State: (${prevOnlineStat})")
		sendEvent(name: "onlineStatus", value: onlineStat?.toString(), descriptionText: "Online Status is: ${onlineStat}", displayed: true, isStateChange: true, state: onlineStat)
		addCheckinReason("onlineStatusChange")
	} else { LogAction("Online Status is: (${onlineStat}) | Original State: (${prevOnlineStat})") }
}

def securityStateEvent(sec) {
	def val = ""
	def oldState = device.currentState("securityState")?.value
	if(sec) { val = sec }
	if(isStateChange(device, "securityState", val?.toString())) {
		Logger("UPDATED | Security State is (${val}) | Original State: (${oldState})")
		sendEvent(name: "securityState", value: val, descriptionText: "Location Security State is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Location Security State is: (${val}) | Original State: (${oldState})") }
}

def isStreamingEvent(isStreaming, override=false) {
	LogAction("isStreamingEvent($isStreaming, $override)...")
	def isOn = device.currentState("isStreaming")?.value
	def isOnline = device.currentState("onlineStatus")?.value
	if(override) { state?.camApiServerData = null }
	else {
		if(state?.camApiServerData && (state?.camApiServerData?.items[0]?.is_streaming != isStreaming) ) {
			Logger("isStreamingEvent: ${isStreaming} | CamData: ${state?.camApiServerData?.items[0]?.is_streaming}")
			//isStreaming = state?.camApiServerData?.items[0]?.is_streaming
			state.camApiServerData = null
		}
	}
	def val = (isOnline?.toString() != "online" ? "offline" : (isStreaming?.toString() == "true") ? "on" : "off")
	//def val = (isStreaming.toString() == "true") ? "on" : (isOnline.toString() != "online" ? "offline" : "off")
	state?.isStreaming = (val == "on") ? true : false
	if(isStateChange(device, "isStreaming", val?.toString())) {
		Logger("UPDATED | Camera Live Video Streaming is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "isStreaming", value: val, descriptionText: "Camera Live Video Streaming is: ${val}", displayed: true, isStateChange: true, state: val)
		sendEvent(name: "switch", value: (val == "on" ? val : "off"), displayed: false)
		cameraStreamNotify(state?.isStreaming)
		addCheckinReason("isStreaming")
	} else { LogAction("Camera Live Video Streaming is: (${val}) | Original State: (${isOn})") }
}

def audioInputEnabledEvent(on) {
	def isOn = device.currentState("audioInputEnabled")?.value
	def val = (on?.toString() == "true") ? "Enabled" : "Disabled"
	state?.audioInputEnabled = val
	if(isStateChange(device, "audioInputEnabled", val?.toString())) {
		Logger("UPDATED | Audio Input Status is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "audioInputEnabled", value: val, descriptionText: "Audio Input Status is: ${val}", displayed: true, isStateChange: true, state: val)
		addCheckinReason("audioInputEnabled")
	} else { LogAction("Audio Input Status is: (${val}) | Original State: (${isOn})") }
}

def videoHistEnabledEvent(on) {
	def isOn = device.currentState("nestAware")?.value
	def val = (on?.toString() == "true") ? "Active" : "Inactive"
	state?.nestAwareActive = (on?.toString() == "true")
	state?.videoHistoryEnabled = val
	if(isStateChange(device, "nestAware", val?.toString())) {
		Logger("UPDATED | Nest Aware Status is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "nestAware", value: val, descriptionText: "Nest Aware Status is: ${val}", displayed: true, isStateChange: true, state: val)
		addCheckinReason("nestAware")
	} else { LogAction("Nest Aware Status is: (${val}) | Original State: (${isOn})") }
}

def publicShareEnabledEvent(on) {
	def isOn = device.currentState("publicShareEnabled")?.value
	def val = (on?.toString() == "true") ? "Enabled" : "Disabled"
	state?.publicShareEnabled = val
	if(isStateChange(device, "publicShareEnabled", val?.toString())) {
		Logger("UPDATED | Public Sharing Status is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "publicShareEnabled", value: val, descriptionText: "Public Sharing Status is: ${val}", displayed: true, isStateChange: true, state: val)
		addCheckinReason("publicShareEnabled")
	} else { LogAction("Public Sharing Status is: (${val}) | Original State: (${isOn})") }
}

def softwareVerEvent(ver) {
	def verVal = device.currentState("softwareVer")?.value
	state?.softwareVer = ver
	if(isStateChange(device, "softwareVer", ver?.toString())) {
		Logger("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
		sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now v${ver}", displayed: false)
		addCheckinReason("softwareVer")
	} else { LogAction("Firmware Version: (${ver}) | Original State: (${verVal})") }
}

def lastEventDataEvent(data) {
	// log.trace "lastEventDataEvent($data)"
	def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
		tf.setTimeZone(getTimeZone())
	def curStartDt = device?.currentState("lastEventStart")?.value ? tf?.format(Date.parse("E MMM dd HH:mm:ss z yyyy", device?.currentState("lastEventStart")?.value?.toString())) : null
	def curEndDt = device?.currentState("lastEventEnd")?.value ? tf?.format(Date.parse("E MMM dd HH:mm:ss z yyyy", device?.currentState("lastEventEnd")?.value?.toString())) : null
	def newStartDt = data?.start_time ? tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time?.toString())) : "Not Available"
	def newEndDt = data?.end_time ? tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time?.toString())) : "Not Available"

	def camMotionZones = state?.camMotionZones && state?.camMotionZones != [] ? state?.camMotionZones : []
	
	def hasPerson = data?.has_person != null ? data?.has_person.toBoolean() : false
	state.motionPerson = hasPerson
	def hasMotion = data?.has_motion != null ? data?.has_motion.toBoolean() : false
	def hasSound = data?.has_sound != null ? data?.has_sound.toBoolean() : false
	def actZones = state?.activityZones
	def evtZoneIds = data?.activity_zone_ids
	def evtZoneNames = null

	String evtType = (!hasMotion ? "Sound Event" : "Motion Event") + "${hasPerson ? " (Person)" : ""}" + "${hasSound ? " (Sound)" : ""}"
	state?.lastEventTypeHtml = (!hasMotion && hasSound ? "Sound Event" : "Motion Event") + "${hasPerson ? "<br>(Person)" : ""}" + "${hasSound ? "<br>(Sound)" : ""}"
	if(actZones && evtZoneIds) {
		evtZoneNames = actZones.findAll { it?.id?.toString() in evtZoneIds }.collect { it?.name }
		def zstr = ""
		def i = 1
		evtZoneNames?.sort().each {
			zstr += "${(i > 1 && i <= evtZoneNames.size()) ? "<br>" : ""}${it}"
			i = i+1
		}
		state?.lastEventZonesHtml = zstr
	}

	def motionZoneOk = isMotionZoneOk(evtZoneIds)
	//log.debug "curStartDt: $curStartDt | curEndDt: $curEndDt || newStartDt: $newStartDt | newEndDt: $newEndDt"

	state.lastEventDate = formatDt2(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time?.toString()), "MMMMM d, yyyy")?.toString()
	state.lastEventTime = "${formatDt2(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time?.toString()), "h:mm:ssa")} to ${formatDt2(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time?.toString()), "h:mm:ssa")}"
	if(state?.lastEventData) { state.lastEventData == null }

	def tryPic = false

	//if(!state?.lastCamEvtData || (curStartDt != newStartDt || curEndDt != newEndDt) || (hasPerson || hasMotion || hasSound) || isStateChange(device, "lastEventType", evtType?.toString()) || isStateChange(device, "lastEventZones", evtZoneNames?.toString())) {
	if(!state?.lastCamEvtData || (curStartDt != newStartDt || curEndDt != newEndDt) || isStateChange(device, "lastEventType", evtType?.toString()) || isStateChange(device, "lastEventZones", evtZoneNames?.toString())) {
	    if(hasPerson || hasMotion || hasSound) {
		sendEvent(name: 'lastEventStart', value: newStartDt, descriptionText: "Last Event Start is ${newStartDt}", displayed: false)
		sendEvent(name: 'lastEventEnd', value: newEndDt, descriptionText: "Last Event End is ${newEndDt}", displayed: false)
		sendEvent(name: 'lastEventType', value: evtType, descriptionText: "Last Event Type was ${evtType}", displayed: false)
		sendEvent(name: 'lastEventZones', value: evtZoneNames?.toString(), descriptionText: "Last Event Zones: ${evtZoneNames}", displayed: false)
		state.lastCamEvtData = ["startDt":newStartDt, "endDt":newEndDt, "hasMotion":hasMotion, "hasSound":hasSound, "hasPerson":hasPerson, "motionOnPersonOnly":(settings?.motionOnPersonOnly == true), "actZones":(data?.activity_zone_ids ?: null), "sentMUpd":false, "sentSUpd":false ]
		tryPic = evtSnapShotOk()
		Logger(state?.enRemDiagLogging ? "?????????????????????????????????????????????" : "???????????????????????????????????????????????????????????????????????????????????????")
		//Logger("???	URL: ${state?.animation_url ?: "None"}")
		Logger("???	Took Snapshot: (${tryPic})")
		if(evtType?.startsWith("Motion Event")) { Logger("???	Zone Motion Filtered: (${(motionZoneOk == false)})") }
		Logger("???	Zones: ${evtZoneNames ?: "None"}")
		Logger("???	End Time: (${newEndDt})")
		Logger("???	Start Time: (${newStartDt})")
		Logger("???	Type: ${evtType}")
		Logger(state?.enRemDiagLogging ? "????????????New Camera Event????????????" : "???????????????????????????New Camera Event????????????????????????")
		addCheckinReason("lastEventData")
	    }
	} else {
		LogAction("Last Event Start Time: (${newStartDt}) - Zones: ${evtZoneNames} | Original State: (${curStartDt})")
		LogAction("Last Event End Time: (${newEndDt}) - Zones: ${evtZoneNames} | Original State: (${curEndDt})")
		LogAction("Last Event Type: (${evtType}) - Zones: ${evtZoneNames}")
	}
	motionSoundEvtHandler(motionZoneOk)
	if(tryPic && motionZoneOk) {
		if(state?.videoHistoryEnabled == "Active" && state?.animation_url) {
			takePicture(state?.animation_url)
		} else {
			takePicture(state?.snapshot_url)
		}
	}
}

def isMotionZoneOk(evtZoneIds) {
	if(state?.camMotionZones && evtZoneIds) {
		if(evtZoneIds?.size()) {
			def res = evtZoneIds?.find { it?.toString() in state?.camMotionZones }
			// log.debug "result: ${res}"
			if(res) { return false}
		}
	}
	return true
}

def motionSoundEvtHandler(zoneOk=true) {
	def data = state?.lastCamEvtData
	if(data) {
		motionEvtHandler(data, zoneOk)
		data = state?.lastCamEvtData
		soundEvtHandler(data)
	}
}

void motionEvtHandler(data, zoneOk) {

	def curMotion = device.currentState("motion")?.stringValue
	def motionStat = "inactive"
	def motionPerStat = "inactive"
	if(state?.restStreaming == true && data && zoneOk != false) {
		if(data?.endDt && data?.hasMotion && !data?.sentMUpd) {
			int t0 = getTimeDiffSeconds(data?.startDt, data?.endDt)
			int t1 = state?.motionSndChgWaitVal ?: 4
			int newDur = Math.min( Math.max(3, t0) , t1)

			t0 = getTimeDiffSeconds(data?.endDt)
			def howRecent = Math.max(1, t0)
			//Logger("MOTION NewDur: ${newDur}    howRecent: ${howRecent}")

			def tt0 = state?.lastCamEvtData
			tt0.sentMUpd = true
			state.lastCamEvtData = tt0
			if(howRecent <= 60) {
				def motGo = (data?.motionOnPersonOnly == true && data?.hasPerson != true) ? false : true
				if(motGo) {
					motionStat = "active"
					if(data?.hasPerson) { motionPerStat = "active" }
					runIn(newDur?.toInteger(), "motionSoundEvtHandler", [overwrite: true])
				}
			}
		}
	}
	// log.trace "motionEvtHandler(zoneOk: $zoneOk) | motionStat: $motionStat | curMotion: $curMotion"
	if(isStateChange(device, "motion", motionStat?.toString()) || isStateChange(device, "motionPerson", motionPerStat?.toString())) {
		Logger("UPDATED | Motion Sensor is: (${motionStat}) | Person: (${motionPerStat}) | Original State: (${curMotion})")
		sendEvent(name: "motion", value: motionStat, descriptionText: "Motion Sensor is: ${motionStat}", displayed: true, /*isStateChange: true,*/ state: motionStat)
		sendEvent(name: "motionPerson", value: motionPerStat, descriptionText: "Motion Person is: ${motionPerStat}", displayed: true, /*isStateChange: true,*/ state: motionPerStat)
		addCheckinReason("motion")
	} else { LogAction("Motion Sensor is: (${motionStat}) | Original State: (${curMotion})") }
}

void soundEvtHandler(data) {
	def curSound = device.currentState("sound")?.stringValue
	def sndStat = "not detected"
	if(state?.restStreaming == true && data) {
		if(data?.endDt && data?.hasSound && !data?.sentSUpd) {
			int t0 = getTimeDiffSeconds(data?.startDt, data?.endDt)
			int t1 = state?.motionSndChgWaitVal ?: 4
			int newDur = Math.min( Math.max(3, t0) , state?.motionSndChgWaitVal)

			t0 = getTimeDiffSeconds(data?.endDt)
			def howRecent = Math.max(1, t0)
			//Logger("SOUND NewDur: ${newDur}    howRecent: ${howRecent}")

			def tt0 = state?.lastCamEvtData
			tt0.sentSUpd = true
			state.lastCamEvtData = tt0
			if(howRecent <= 60) {
				sndStat = "detected"
				runIn(newDur?.toInteger(), "motionSoundEvtHandler", [overwrite: true])
			}
		}
	}
	if(isStateChange(device, "sound", sndStat?.toString())) {
		Logger("UPDATED | Sound Sensor State: (${sndStat}) | Original State: (${curSound})")
		sendEvent(name: "sound", value: sndStat, descriptionText: "Sound Sensor is: ${sndStat}", displayed: true, isStateChange: true, state: sndStat)
		addCheckinReason("sound")
	} else { LogAction("Sound Sensor State: (${sndStat}) | Original State: (${curSound})") }
}

def imageEvent(url) {
	def clearUrl = true
	if(state?.isOnline && state?.isStreaming && url != null && url != "") {
		if(url?.startsWith("https://")) {
			def curImage = device.currentState("image")?.stringValue
			state.snapshot_url = url
			clearUrl = false
			if(isStateChange(device, "image", url?.toString())) {
				LogAction("UPDATED | Image Url: (${url}) | Original State: (${curImage})")
				sendEvent(name: "image", value: url, descriptionText: "Image URL ${url}", displayed: false, isStateChange: true, state: url)
			}
		}
	}
	if(clearUrl) {
		state.snapshot_url = null
		sendEvent(name: "image", value: "", descriptionText: "Image URL Cleared", displayed: false)
	}
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
	def dVal = debug ? "On" : "Off"
	state?.debugStatus = dVal
	state?.debug = debug?.toBoolean() ? true : false
	if(isStateChange(device, "debugOn", dVal?.toString())) {
		Logger("UPDATED | Device Debug Logging is: (${dVal}) | Original State: (${val})")
		sendEvent(name: 'debugOn', value: dVal, displayed: false)
		addCheckinReason("debugOn")
	} else { LogAction("Device Debug Logging is: (${dVal}) | Original State: (${val})") }
}

def apiStatusEvent(issue) {
	def curStat = device.currentState("apiStatus")?.value
	def newStat = issue
	state.apiStatus = newStat
	if(isStateChange(device, "apiStatus", newStat?.toString())) {
		Logger("UPDATED | API Status is: (${newStat}) | Original State: (${curStat})")
		sendEvent(name: "apiStatus", value: newStat, descriptionText: "API Status is: ${newStat}", displayed: true, isStateChange: true, state: newStat)
		addCheckinReason("apiStatus")
	} else { LogAction("API Status is: (${newStat}) | Original State: (${curStat})") }
}

def lastUpdatedEvent(sendEvt=false) {
	def now = new Date()
	def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
	tf.setTimeZone(getTimeZone())
	def lastDt = "${tf?.format(now)}"
	state?.lastUpdatedDt = lastDt?.toString()
	state?.lastUpdatedDtFmt = formatDt(now)
	if(sendEvt && state?.isOnline) {
		LogAction("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
		sendEvent(name: 'lastUpdatedDt', value: formatDt(now)?.toString(), displayed: false, isStateChange: true)
		addCheckinReason("lastUpdatedDt")
	}
}

def videoQualityEvent() {
	if(!state?.camApiServerData) { return }
	def camData = state?.camApiServerData?.items[0]?.capabilities?.findAll { it?.startsWith("streaming.data-usage-tier") }.collect { it as String }
	log.debug "camData: $camData"
	/*
		Nest Cam IQ (Indoor/Outdoor)
		* Low: 100GB
		* Medium Low: 200GB
		* Medium High: 300GB
		* High: 400GB

		Nest Cam Indoor/Outdoor
		* Low: 30GB
		* Medium: 120GB
		* High: 300GB

		Dropcam, Dropcam HD
		* Low: 30GB
		* Medium: 120GB

		Nest Hello Doorbell
		* Low: 50GB
		* Medium: 150GB
		* High: 300GB

	*/
	def vals = []
	camData?.each { item->
		log.debug "${item?.split(".")}"

		// if(t[2]?.isNumber()) {
		// 	vals.push(t[2] as Integer)
		// }
	}
	log.debug "vals: ${vals} | max: "
}

def findCameraModel() {
	// if(!state?.camApiServerData) { return }
	// def camData = state?.camApiServerData?.items[0]?.capabilities?.findAll { it?.startsWith("streaming.cameraprofile") }

}

def publicShareUrlEvent(url) {
	//log.trace "publicShareUrlEvent($url)"
	if(url) {
		if(!state?.public_share_url || state?.public_share_url != url) {
			state?.public_share_url = url
			Logger("publicShareUrlEvent url changed ${url}")
			state.camApiServerData = null
		}
		def pubVidId = getPublicVidID()
		def lastVidId = state?.lastPubVidId
		//log.debug "Url: $url | Url(state): ${state?.public_share_url} | pubVidId: $pubVidId | lastVidId: $lastVidId | camUUID: ${state?.camUUID}"
		if(lastVidId == null || lastVidId?.toString() != pubVidId?.toString()) {
			//state?.public_share_url = url
			state?.lastPubVidId = pubVidId
		}
		if(!state?.camUUID) {
			getCamUUID(pubVidId)
		} else {
			def camData
			def ldtSec = getTimeDiffSeconds(state?.lastGetCamApiServerData)
			if(state?.camUUID && (state?.camApiServerData == null || ldtSec > (180*60)) ) {
				camData = getCamApiServerData(state?.camUUID)
			}
			if(camData && state?.lastCamApiServerData != camData) {
				state?.lastCamApiServerData = camData
				log.trace "publicShareUrlEvent($url) camData Changed"
			}
		}
	} else {
		//Logger("Url: $url | Url(state): ${state?.public_share_url} | pubVidId: ${state.pubVidId} | lastVidId: ${state.lastPubVidId} | camUUID: ${state?.camUUID} | camApiServerData ${state?.camApiServerData} | animation_url ${state?.animation_url} | snapshot_url ${state?.snapshot_url}", "warn")
		if(state?.public_share_url || state?.pubVidId || state?.lastPubVidId || state?.camUUID || state?.camApiServerData) {
			state?.public_share_url = null
			state?.pubVidId = null
			state?.lastPubVidId = null
			state?.camUUID = null
			state?.camApiServerData = null
			state?.animation_url = null
			state?.snapshot_url = null
			Logger("Cleared Cached Camera State Info because Sharing has been disabled or the Public ID is no longer available...", "warn")
		}
	}
}

def getPublicVidID() {
	def id = null
	if(!state?.pubVidId && state?.public_share_url) {
		id = state?.public_share_url.tokenize('/')[3].toString()
		state?.pubVidId = id
	} else {
		id = state?.pubVidId
	}
	return id
}

def getRecTimeDesc(val) {
	def result = null
	if(val && val instanceof Integer) {
		if(val.toInteger() > 24) {
			def nVal = (val/24).toDouble().round(0)
			result = "${nVal.toInteger()} days"
		} else {
			result = "${val} hours"
		}
	}
	return result
}

def sendNofificationMsg(msg, msgType, recips = null, sms = null, push = null) {
	if(msg && msgType) { parent?.sendMsg(msg, msgType, recips, sms, push) }
}

def cameraStreamNotify(streaming) {
	if(streaming == null || state?.streamMsg != true) { return }
	parent?.cameraStreamNotify(this, streaming)
}

def getHealthStatus(lower=false) {
	def res = device?.getStatus()
	if(lower) { return res?.toString()?.toLowerCase() }
	return res?.toString()
}

def healthNotifyOk() {
	def lastDt = state?.lastHealthNotifyDt
	if(lastDt) {
		def ldtSec = getTimeDiffSeconds(lastDt)
		def t0 = state?.healthMsgWait ?: 3600
		if(ldtSec < t0) {
			return false
		}
	}
	return true
}

def checkHealth() {
	def isOnline = (getHealthStatus() == "ONLINE") ? true : false
	if(state?.healthMsg != true || state?.healthInRepair == true || isOnline) { return }
	if(healthNotifyOk()) {
		def now = new Date()
		parent?.deviceHealthNotify(this, isOnline)
		state.lastHealthNotifyDt = formatDt(now)
	}
}

/************************************************************************************************
|									DEVICE COMMANDS     										|
*************************************************************************************************/
void chgStreaming() {
	def cur = device?.currentState("isStreaming")?.value?.toString()
	if(cur == "on" || cur == "unavailable" || !cur) {
		streamingOff(true)
	} else {
		streamingOn(true)
	}
}

void streamingOn(manChg=false) {
	try {
		log.trace "streamingOn..."
		if(parent?.setCamStreaming(this, "true")) {
			isStreamingEvent(true, true)
			if(manChg) { incManStreamChgCnt() }
			else { incProgStreamChgCnt() }
		}

	} catch (ex) {
		log.error "streamingOn Exception: ${ex?.message}", ex
		exceptionDataHandler(ex?.message, "streamingOn")
	}
}

void streamingOff(manChg=false) {
	try {
		log.trace "streamingOff..."
		if(parent?.setCamStreaming(this, "false")) {
			isStreamingEvent(false, true)
			if(manChg) { incManStreamChgCnt() }
			else { incProgStreamChgCnt() }
		}
	} catch (ex) {
		log.error "streamingOff Exception: ${ex?.message}", ex
		exceptionDataHandler(ex?.message, "streamingOff")
	}
}

void on() {
	streamingOn()
}

void off() {
	streamingOff()
}

void take() {
	takePicture(state?.snapshot_url)
}

void mute() {
	Logger("Nest API does not allow turning microphone off...")
}

void unmute() {
	Logger("Nest API does not allow turning microphone on...")
}

private getPictureName() {
	def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
	getCamUUID(getPublicVidID()) + "_$pictureUuid" + ".jpg"
}

private getImageWidth() {
	return 1280
}

private takePicture(String url) {
	try {
		if(state?.isOnline && state?.isStreaming) {
			if(url?.startsWith("https://")) {
				ByteArrayInputStream imageBytes
				def params = [
					uri: url,
					requestContentType: "application/x-www-form-urlencoded"
				]
				httpGet(params) { resp ->
					imageBytes = resp?.data
					if (imageBytes) {
						storeImage(getPictureName(), imageBytes)
						return true
					}
				}
			} else {
				def sUrl = url ? "${url}" : "null"
				Logger("takePicture: non-standard url received (${sUrl}), public share enabled: (${state?.publicShareEnabled})", "error")
			}
		} else {
	      		Logger("takePicture: Camera is not online (${!state?.isOnline}) or not streaming (${!state?.isStreaming})", "error")
		}
	} catch (ex) {
		log.error "takePicture Exception: ${ex?.message}", ex
		exceptionDataHandler(ex?.message, "takePicture")
	}
	return false
}

/************************************************************************************************
|							EXCEPTION HANDLING & LOGGING FUNCTIONS								|
*************************************************************************************************/
def lastN(String input, n) {
  return n > input?.size() ? null : n ? input[-n..-1] : ''
}

void Logger(msg, logType = "debug") {
	def smsg = state?.showLogNamePrefix ? "${device.displayName} (v${devVer()}) | ${msg}" : "${msg}"
	def theId = lastN(device.getId().toString(),5)
	if(state?.enRemDiagLogging) {
		parent.saveLogtoRemDiagStore(smsg, logType, "Camera-${theId}")
	} else {
		switch (logType) {
			case "trace":
				log.trace "${smsg}"
				break
			case "debug":
				log.debug "${smsg}"
				break
			case "info":
				log.info "${smsg}"
				break
			case "warn":
				log.warn "${smsg}"
				break
			case "error":
				log.error "${smsg}"
				break
			default:
				log.debug "${smsg}"
				break
		}
	}
}

// Local Application Logging
void LogAction(msg, logType = "debug") {
	if(state?.debug) {
		Logger(msg, logType)
	}
}

 //This will Print logs from the parent app when added to parent method that the child calls
def log(message, level = "trace") {
	def smsg = "PARENT_Log>> " + message
	LogAction(smsg, level)
	return null // always child interface call with a return value
}

def exceptionDataHandler(String msg, String methodName) {
	if(state?.allowDbException == false) {
		return
	} else {
		if(msg && methodName) {
			def msgString = "${msg}"
			parent?.sendChildExceptionData("camera", devVer(), msgString, methodName)
		}
	}
}

def getTimeDiffSeconds(strtDate, stpDate=null, methName=null) {
	//LogTrace("[GetTimeDiffSeconds] StartDate: $strtDate | StopDate: ${stpDate ?: "Not Sent"} | MethodName: ${methName ?: "Not Sent"})")
	if((strtDate && !stpDate) || (strtDate && stpDate)) {
		def now = new Date()
		def stopVal = stpDate ? stpDate.toString() : formatDt(now)
		def start = Date.parse("E MMM dd HH:mm:ss z yyyy", strtDate).getTime()
		def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", stopVal).getTime()
		def diff = (int) (long) (stop - start) / 1000
		return diff
	} else { return null }
}

def incHtmlLoadCnt() 		{ state?.htmlLoadCnt = (state?.htmlLoadCnt ? state?.htmlLoadCnt.toInteger()+1 : 1) }
def incManStreamChgCnt() 	{ state?.manStreamChgCnt = (state?.manStreamChgCnt ? state?.manStreamChgCnt.toInteger()+1 : 1) }
def incProgStreamChgCnt() 	{ state?.progStreamChgCnt = (state?.progStreamChgCnt ? state?.progStreamChgCnt.toInteger()+1 : 1) }
def incVideoBtnTapCnt()		{ state?.videoBtnTapCnt = (state?.videoBtnTapCnt ? state?.videoBtnTapCnt.toInteger()+1 : 1); return ""; }
def incImageBtnTapCnt()		{ state?.imageBtnTapCnt = (state?.imageBtnTapCnt ? state?.imageBtnTapCnt.toInteger()+1 : 1); return ""; }
def incEventBtnTapCnt()		{ state?.eventBtnTapCnt = (state?.eventBtnTapCnt ? state?.eventBtnTapCnt.toInteger()+1 : 1); return ""; }
def incInfoBtnTapCnt()		{ state?.infoBtnTapCnt = (state?.infoBtnTapCnt ? state?.infoBtnTapCnt.toInteger()+1 : 1); return ""; }

def getMetricCntData() {
	return 	[
		camManStrChgCnt:(state?.manStreamChgCnt ?: 0), camProgStrChgCnt:(state?.progStreamChgCnt ?: 0), camHtmlLoadedCnt:(state?.htmlLoadCnt ?: 0)//,
		//camVidBtnTapCnt:(state?.videoBtnTapCnt ?: 0), camImgBtnTapCnt:(state?.imageBtnTapCnt ?: 0), camEvtBtnTapCnt:(state?.eventBtnTapCnt ?: 0),
		//camInfoBtnCnt:(state?.infoBtnTapCnt ?: 0)
	]
}

/************************************************************************************************
|										OTHER METHODS     										|
*************************************************************************************************/

def getDtNow() {
	def now = new Date()
	return formatDt(now)
}

def formatDt(dt, mdy = false) {
	//log.trace "formatDt($dt, $mdy)..."
	def formatVal = mdy ? (state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a") : "E MMM dd HH:mm:ss z yyyy"
	def tf = new SimpleDateFormat(formatVal)
	if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
	else {
		Logger("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...")
	}
	return tf.format(dt)
}

def formatDt2(dt, fmt=null) {
	//log.trace "formatDt($dt, $mdy)..."
	def tf = new SimpleDateFormat(fmt)
	if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
	else {
		Logger("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...")
	}
	return tf.format(dt)
}

def epochToTime(tm) {
	def tf = new SimpleDateFormat("h:mm a")
		tf?.setTimeZone(getTimeZone())
	return tf.format(tm)
}

def isTimeBetween(start, end, now, tz) {
	def startDt = Date.parse("E MMM dd HH:mm:ss z yyyy", start).getTime()
	def endDt = Date.parse("E MMM dd HH:mm:ss z yyyy", end).getTime()
	def nowDt = Date.parse("E MMM dd HH:mm:ss z yyyy", now).getTime()
	def result = false
	if(nowDt > startDt && nowDt < endDt) {
		result = true
	}
	//def result = timeOfDayIsBetween(startDt, endDt, nowDt, tz) ? true : false
	return result
}

def getFileBase64(url, preType, fileType) {
	try {
		def params = [uri: url, contentType: "$preType/$fileType"]
		httpGet(params) { resp ->
			if(resp?.status == 200) {
				if(resp.data) {
					def respData = resp?.data
					byte[] byteData = resp?.data?.getBytes()
					String enc = byteData?.encodeBase64()
					// log.debug "enc: ${enc}"
					return enc ? "data:${preType}/${fileType};base64,${enc?.toString()}" : null
				}
			} else {
				LogAction("getFileBase64 Resp: ${resp?.status} ${url}", "error")
				exceptionDataHandler("resp ${ex?.response?.status} ${url}", "getFileBase64")
				return null
			}
		}
	} catch (ex) {
		if(ex instanceof groovyx.net.http.ResponseParseException) {
			if(ex?.statusCode != 200) {
				LogAction("getFileBase64 Resp: ${ex?.statusCode} ${url}", "error")
				log.error "getFileBase64 Exception:", ex
			}
		} else if(ex instanceof groovyx.net.http.HttpResponseException && ex?.response) {
			LogAction("getFileBase64 Resp: ${ex?.response?.status} ${url}", "error")
			exceptionDataHandler("${ex?.response?.status} ${url}", "getFileBase64")
		} else {
			log.error "getFileBase64 Exception:", ex
			exceptionDataHandler(ex, "getFileBase64")
		}
		return null
    }
}

def getImg(imgName) {
	return imgName ? "https://cdn.rawgit.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : ""
}

def getWebData(params, desc, text=true) {
	try {
		//Logger("getWebData: ${desc} data", "info")
		httpGet(params) { resp ->
			if(resp.data) {
				if(text) {
					return resp?.data?.text.toString()
				} else { return resp?.data }
			}
		}
	}
	catch (ex) {
		if(ex instanceof groovyx.net.http.HttpResponseException) {
			Logger("${desc} file not found", "warn")
		} else {
			log.error "getWebData(params: $params, desc: $desc, text: $text) Exception:", ex
		}
		//sendExceptionData(ex, "getWebData")
		return "${label} info not found"
	}
}
def gitRepo()		{ return "tonesto7/nest-manager"}
def gitBranch()		{ return state?.isBeta ? "beta" : "master" }
def gitPath()		{ return "${gitRepo()}/${gitBranch()}"}
def devVerInfo()	{ return getWebData([uri: "https://raw.githubusercontent.com/${gitPath()}/Data/changelog_cam.txt", contentType: "text/plain; charset=UTF-8"], "changelog") }

//this scrapes the public nest cam page for its unique id for using in render html tile
include 'asynchttp_v1' //<<<<<This is currently in Beta

def getCamUUID(pubVidId) {
	try {
		if(pubVidId) {
			if(!state?.isStreaming) {
				Logger("getCamUUID: Your Camera's currently not streaming so we are unable to get the required ID.  Once streaming is enabled the ID will be collected...", "warn")
				return null
			}
			else if(state?.isStreaming && (state?.lastPubVidId == null || state?.camUUID == null || state?.lastPubVidId != pubVidId)) {
				def params = [
					uri: "https://video.nest.com/live/${pubVidId}",
					requestContentType: 'text/html'
				]
				asynchttp_v1.get('camPageHtmlRespMethod', params)
			} else {
				return state?.camUUID
			}
		} else { Logger("getCamUUID: Your Camera's PublicVideoId was not found!!! Please make sure you have public video sharing enabled under your Cameras settings in the Nest Mobile App...", "warn") }
	} catch (ex) {
		log.error "getCamUUID Exception: ${ex?.message}", ex
		exceptionDataHandler(ex?.message, "getCamUUID")
	}
}

def camPageHtmlRespMethod(response, data) {
	//log.debug "camPageHtmlRespMethod: ${response?.status}, ${response.getData()}"
	if(response?.status != 408) {
		def rData = response.getData()
		//log.debug
		def url = (rData =~ /<meta.*property="og:image".*content="(.*)".*/)[0][1]
		// log.debug "url: $url"
		def uuid = (url =~ /(\?|\&)([^=]+)\=([^&]+)/)[0][3]
		// log.debug "UUID: ${uuid}"
		state.camUUID = uuid
	} else { return }
}

def getCamApiServerData(camUUID) {
	// LogTrace("getCamApiServerData($camUUID)")
	try {
		if(camUUID) {
			def params = [
				uri: "https://www.dropcam.com/api/v1/cameras.get?id=${camUUID}"
			]
			httpGet(params)  { resp ->
				if(resp?.status == 200) {
					LogAction("getCamApiServerData resp: (status: ${resp?.status}) | data: ${resp?.data}")
					if(resp?.data) {
						state?.camApiServerData = resp?.data
						state.lastGetCamApiServerData = getDtNow()
						return resp?.data
					}
				} else { Logger("getCamApiServerData Resp: ${resp?.status}....", "warn") }
			}
		} else { Logger("getCamApiServerData camUUID is missing....", "warn") }
	}
	catch (ex) {
		def s = ex?.message?.toString()
		log.error "getCamApiServerData Exception: ${s}", ex
		exceptionDataHandler(s, "getCamApiServerData")
	}
	return null
}

def getStreamHostUrl() {
	if(!state?.camApiServerData) { return null }
	def res = state?.camApiServerData?.items[0]?.live_stream_host
	def data = res?.toString().replaceAll("\\[|\\]", "")
	//log.debug "getStreamHostUrl: $data"
	return data ?: null
}

String getCamPlaylistURL() {
	String hUrl = getStreamHostUrl()
	if(hUrl && state?.camUUID) { return "https://${hUrl}/nexus_aac/${state?.camUUID}/playlist.m3u8" }
	return null
}

def getCamApiServer() {
	if(!state?.camApiServerData) { return null }
	def res = state?.camApiServerData?.items[0]?.nexus_api_http_server
	def data = res?.toString().replaceAll("\\[|\\]", "")
	//log.debug "getCamApiServer: $data"
	return data ?: null
}

def androidDisclaimerMsg() {
	if(state?.mobileClientType == "android" && !state?.androidDisclaimerShown) {
		state.androidDisclaimerShown = true
		return """<div class="androidAlertBanner">FYI... The Android Client has a bug with reloading the HTML a second time.\nIt will only load once!\nYou will be required to completely close the client and reload to view the content again!!!</div>"""
	} else { return "" }
}

def getChgLogHtml() {
	def chgStr = ""
	//log.debug "shownChgLog: ${state?.shownChgLog}"
	if(!state?.shownChgLog == true) {
		chgStr = """
			<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
			<script src="https://cdnjs.cloudflare.com/ajax/libs/vex-js/3.1.1/js/vex.combined.min.js"></script>
			<script>
				\$(document).ready(function() {
				    vex.dialog.alert({
						unsafeMessage: `<h3 style="background-color: transparent;">What\'s New with the Camera</h3>
						<div style="padding: 0 5px 0 5px; text-align: left;">
							${devVerInfo()}
						</div>`
				    , className: 'vex-theme-top'})
				});
			</script>
		"""
		state?.shownChgLog = true
	}
	return chgStr
}

def hasHtml() { return true }

def getCamIframHtml() {
	def id = getPublicVidID() ? getPublicVidID() : null
	return id ? """
		<div class="embed-responsive embed-responsive-16by9">
			<iframe type="text/html" frameborder="0" width="480" height="394" src="//video.nest.com/embedded/live/${id}?autoplay=0" allowfullscreen></iframe>
		</div>
	""" : ""
}

def getCamHtml() {
	try {
		// These are used to determine the URL for the nest cam stream
		def updateAvail = !state.updateAvailable ? "" : """<div class="greenAlertBanner">Device Update Available!</div>"""
		def clientBl = state?.clientBl ? """<div class="brightRedAlertBanner">Your Manager client has been blacklisted!\nPlease contact the Nest Manager developer to get the issue resolved!!!</div>""" : ""
		def pubVidUrl = state?.public_share_url
		def camHtml = (pubVidUrl && state?.camUUID && state?.isStreaming && state?.isOnline) ? showCamHtml(false) : hideCamHtml()
		def devBrdCastData = state?.devBannerData ?: null
		def devBrdCastHtml = ""
		def refreshBtnHtml = state.mobileClientType == "ios" ? """<div class="pageFooterCls"><p class="slideFooterText">Swipe/Tap to Change Slide</p><button type="button" class="btn btn-info slideFooterBtn" onclick="reloadPage()"><span>&#10227;</span> Refresh</button></div>""" : ""
		if(devBrdCastData) {
			def curDt = Date.parse("E MMM dd HH:mm:ss z yyyy", getDtNow())
			def expDt = Date.parse("E MMM dd HH:mm:ss z yyyy", devBrdCastData?.expireDt?.toString())
			if(curDt < expDt) {
				devBrdCastHtml = """
					<div class="orangeAlertBanner">
						<div>Message from the Developer:</div>
						<div style="font-size: 4.6vw;">${devBrdCastData?.message}</div>
					</div>
				"""
			}
		}
		def mainHtml = """
		<!DOCTYPE html>
		<html>
			<head>
				<meta charset="utf-8"/>
				<meta http-equiv="cache-control" content="max-age=0"/>
				<meta http-equiv="cache-control" content="no-cache"/>
				<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
				<meta http-equiv="pragma" content="no-cache"/>
				<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0">

				<link rel="stylesheet" type="text/css" href="https://cdn.rawgit.com/tonesto7/nest-manager/master/Documents/css/ST-HTML.min.css"/>
				<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.3.3/css/swiper.min.css" />
				<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/vex-js/3.1.1/css/vex.min.css" async/>
				<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/vex-js/3.1.1/css/vex-theme-top.min.css" async />
				<style>
					.swiper-container {
						width: 95%;
						min-height: 300px;
						padding: 0 10px !important;
					}

					.swiper-pagination-bullet {
						width: 20px;
						height: 20px;
						text-align: center;
						line-height: 20px;
						font-size: 10px;
						color: #000;
						opacity: 1;
						background: rgba(0, 0, 0, 0.2);
					}

					.swiper-pagination-bullet-active {
						color: #fff;
						background: #007aff;
					}
				</style>
			</head>
			<body>
				${getChgLogHtml()}
				${androidDisclaimerMsg()}
				${devBrdCastHtml}
				${clientBl}
				${updateAvail}
				<div class="swiper-container" style="max-width: 100%; overflow: hidden;">
					<div class="swiper-wrapper">
						${camHtml}
						<div class="swiper-slide">
						  <section class="sectionBg">
							<h3>Device Info</h3>
							<table class="devInfo">
							  <col width="40%">
							  <col width="40%">
							  <thead>
								<th>Network Status</th>
								<th>API Status</th>
							  </thead>
							  <tbody>
								<tr>
								  <td${state?.onlineStatus != "Online" ? """ class="redText" """ : ""}>${state?.onlineStatus}</td>
								  <td${state?.apiStatus != "Good" ? """ class="orangeText" """ : ""}>${state?.apiStatus}</td>
								</tr>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBg">
							<table class="devInfo">
							  <col width="33%">
							  <col width="33%">
							  <col width="33%">
							  <thead>
								<th>Nest Aware</th>
								<th>Public Video</th>
								<th>Mic Status</th>
							  </thead>
							  <tbody>
								<tr>
								  <td>${state?.nestAwareActive == true ? "Active" : "Not Active"}</td>
								  <td>${state?.publicShareEnabled?.toString()}</td>
								  <td>${state?.audioInputEnabled?.toString()}</td>
								</tr>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBg">
							<table class="devInfo">
							  <col width="40%">
							  <col width="20%">
							  <col width="40%">
							  <thead>
								<th>Firmware</th>
								<th>Debug</th>
								<th>Device Type</th>
							  </thead>
							  <tbody>
								  <td>v${state?.softwareVer?.toString()}</td>
								  <td>${state?.debugStatus}</td>
								  <td>${state?.devTypeVer?.toString()}</td>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBg">
							<table class="devInfo">
							   <thead>
								 <th>Last Online Change</th>
								 <th>Data Last Received</th>
							   </thead>
							   <tbody>
								 <tr>
								   <td class="dateTimeText">${state?.lastConnection?.toString()}</td>
								   <td class="dateTimeText">${state?.lastUpdatedDt?.toString()}</td>
								 </tr>
							   </tbody>
							 </table>
						   </section>
						</div>
					</div>
					<div class="swiper-pagination"></div>
					<div style="text-align: center; padding: 25px 0;"></div>
				</div>
				<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.3.3/js/swiper.min.js"></script>
				<script>
					var mySwiper = new Swiper ('.swiper-container', {
						direction: 'horizontal',
						roundLengths: true,
						updateOnImagesReady: true,
						loop: true,
						slidesPerView: '1',
						grabCursor: true,
						centeredSlides: true,
						spaceBetween: 100,
						autoHeight: true,
						iOSEdgeSwipeDetection: true,
						parallax: true,
						slideToClickedSlide: true,
						effect: 'coverflow',
						coverFlowEffect: {
						  rotate: 50,
						  stretch: 0,
						  depth: 100,
						  modifier: 1,
						  slideShadows: false
						},
						on: {
							tap: function() {
								this.slideNext()
							},
						},
						pagination: {
							el: '.swiper-pagination',
							hideOnClick: false,
							clickable: true,
							renderBullet: function (index, className) {
								return '<span class="' + className + '">' + (index + 1) + '</span>';
							},
						}
					});
					function reloadPage() {
						window.location.reload();
					}
				</script>
				${refreshBtnHtml}
			</body>
		</html>
		"""
		incHtmlLoadCnt()
		render contentType: "text/html", data: mainHtml, status: 200
	}
	catch (ex) {
		log.error "getCamHtml Exception: ${ex?.message}", ex
		exceptionDataHandler(ex?.message, "getCamHtml")
	}
}

def getDeviceTile(devNum) {
	try {
		def updateAvail = !state.updateAvailable ? "" : """<div class="greenAlertBanner">Device Update Available!</div>"""
		def clientBl = state?.clientBl ? """<div class="brightRedAlertBanner">Your Manager client has been blacklisted!\nPlease contact the NST Manager developer to get the issue resolved!!!</div>""" : ""
		def pubVidUrl = state?.public_share_url
		def camHtml = (pubVidUrl && state?.camUUID && state?.isStreaming && state?.isOnline) ? showCamHtml(true) : hideCamHtml()
		def mainHtml = """
			${clientBl}
			${updateAvail}
			${getCamIframHtml()}
			<div class="device">
				<div class="swiper-container-${devNum}" style="max-width: 100%; overflow: hidden;">
					<div class="swiper-wrapper" style="height: auto;">
						${camHtml}
						<div class="swiper-slide">
						  <section class="sectionBgTile">
							<h3>Device Info</h3>
							<table class="devInfoTile centerText">
							  <col width="40%">
							  <col width="40%">
							  <thead>
								<th>Network Status</th>
								<th>API Status</th>
							  </thead>
							  <tbody>
								<tr>
								  <td${state?.onlineStatus != "Online" ? """ class="redText" """ : ""}>${state?.onlineStatus}</td>
								  <td${state?.apiStatus != "Good" ? """ class="orangeText" """ : ""}>${state?.apiStatus}</td>
								</tr>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBgTile">
							<table class="devInfoTile centerText">
							  <col width="33%">
							  <col width="33%">
							  <col width="33%">
							  <thead>
								<th>Nest Aware</th>
								<th>Public Video</th>
								<th>Mic Status</th>
							  </thead>
							  <tbody>
								<tr>
								  <td>${state?.nestAwareActive == true ? "Active" : "Not Active"}</td>
								  <td>${state?.publicShareEnabled?.toString()}</td>
								  <td>${state?.audioInputEnabled?.toString()}</td>
								</tr>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBgTile">
							<table class="devInfoTile centerText">
							  <col width="40%">
							  <col width="20%">
							  <col width="40%">
							  <thead>
								<th>FW Version</th>
								<th>Debug</th>
								<th>Device Type</th>
							  </thead>
							  <tbody>
								  <td>v${state?.softwareVer?.toString()}</td>
								  <td>${state?.debugStatus}</td>
								  <td>${state?.devTypeVer?.toString()}</td>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBgTile">
							<table class="devInfoTile centerText">
							   <thead>
								 <th>Last Online Change</th>
								 <th>Data Last Received</th>
							   </thead>
							   <tbody>
								 <tr>
								   <td class="dateTimeTextTile">${state?.lastConnection?.toString()}</td>
								   <td class="dateTimeTextTile">${state?.lastUpdatedDt?.toString()}</td>
								 </tr>
							   </tbody>
							 </table>
						   </section>
						</div>
					</div>
					<div style="text-align: center; padding: 20px;">
						<p class="slideFooterTextTile">Swipe/Drag to Change Slide</p>
					</div>
					<div class="swiper-pagination"></div>
				</div>
			</div>
			<script>
				var mySwiper${devNum} = new Swiper ('.swiper-container-${devNum}', {
					direction: 'horizontal',
					roundLengths: true,
					updateOnImagesReady: true,
					loop: true,
					slidesPerView: '1',
					grabCursor: true,
					centeredSlides: true,
					spaceBetween: 100,
					autoHeight: true,
					iOSEdgeSwipeDetection: true,
					parallax: true,
					slideToClickedSlide: true,
					effect: 'coverflow',
					coverFlowEffect: {
						rotate: 50,
						stretch: 0,
						depth: 100,
						modifier: 1,
						slideShadows: false
					},
					on: {
						tap: function() {
							this.slideNext()
						},
					},
					pagination: {
						el: '.swiper-pagination',
						hideOnClick: false,
						clickable: true,
						renderBullet: function (index, className) {
							return '<span class="' + className + '">' + (index + 1) + '</span>';
						},
					}
				});
			</script>
		"""
		return mainHtml
	}
	catch (ex) {
		log.error "getDeviceTile Exception: ${ex?.message}", ex
		exceptionDataHandler(ex?.message, "getDeviceTile")
	}
}

def showCamHtml(tile=false) {
	def pubVidUrl = state?.public_share_url
	if(!state?.camUUID) { getCamUUID(getPublicVidID()) }
	def camUUID = state?.camUUID
	if(camUUID && state?.camApiServerData == null) { getCamApiServerData(camUUID) }

	def apiServer = getCamApiServer()

	def liveStreamURL = getStreamHostUrl()
	def camImgUrl = "${apiServer}/get_image?uuid=${camUUID}&width=410"
	def camPlaylistUrl = "https://${liveStreamURL}/nexus_aac/${camUUID}/playlist.m3u8"

	def animationUrl = state?.animation_url ?: null
	def pubSnapUrl = state?.snapshot_url ?: null

	def vidBtn = (!state?.isStreaming || !liveStreamURL) ? "" : """<a href="#" onclick="toggle_visibility('liveStream');" class="button yellow">Live Video</a>"""
	def imgBtn = (!state?.isStreaming || !pubSnapUrl) ? "" : """<a href="#" onclick="toggle_visibility('still');" class="button blue">Still Image</a>"""
	def lastEvtBtn = (!state?.isStreaming || !animationUrl) ? "" : """<a href="#" onclick="toggle_visibility('animation');" class="button red">Last Event</a>"""

	def data = """
		<div class="swiper-slide">
			${androidDisclaimerMsg()}
			<div>
				<section class="${tile ? "sectionBgTile" : "sectionBg"} centerText">
					<h3>Last Camera Event</h3>
					<table class="${tile ? "devInfoTile" : "devInfo"}">
					  <col width="45%">
					  <col width="55%">
					  <tbody>
						<tr>
						  <td>${state?.lastEventDate ?: "Not Available"}</td>
						  <td>${state?.lastEventTime ?: ""}</td>
						</tr>
					  </tbody>
					</table>
				</section>
				<img src="${animationUrl}" width="100%"/>
				<section class="${tile ? "sectionBgTile" : "sectionBg"} centerText">
					<table class="${tile ? "devInfoTile" : "devInfo"}">
					  <col width="45%">
					  <col width="55%">
					  <thead>
						<th>Event Type</th>
						<th>Event Zone(s)</th>
					  </thead>
					  <tbody>
						<tr>
						  <td style="vertical-align:top;">${state?.lastEventTypeHtml ?: "Unknown"}</td>
						  <td style="vertical-align:top;">${state?.lastEventZonesHtml ?: "Unknown"}</td>
						</tr>
					  </tbody>
					</table>
				</section>
			</div>
		</div>
		<div class="swiper-slide">
			<section class="${tile ? "sectionBgTile" : "sectionBg"}">
				<h3>Still Image</h3>
				<table class="${tile ? "devInfoTile" : "devInfo"} centerText">
				  <tbody>
					<tr>
					  <td>
					  	<img src="${pubSnapUrl}" width="100%"/>
					  	<h4 style="background: #696969; color: #f5f5f5; padding: 4px;">FYI: This image is only refreshed when this window is generated...</h4>
					  </td>
					</tr>
				  </tbody>
				</table>
			</section>
		</div>
	"""
}

def hideCamHtml() {
	def tClass = 'style="background-color: #bd2828;"'
	def bClass = 'style="background-color: transparent; color: #bd2828;  text-shadow: 0px 0px 0px #bd2828; padding: 60px 30px 60px 30px;"'
	def d = """<div class="swiper-slide"><section class="sectionBg">"""
	if(!state?.isStreaming && state?.isOnline) {
		d += """<h3 ${tClass}>Live video streaming is Off</h3><br><h3 ${bClass}>Please Turn it back on and refresh this page...</h3>"""
	}
	else if(!state?.camUUID) {
		d += """<h3 ${tClass}>Camera ID Not Found...</h3><br><h3 ${bClass}>If this is your first time opening this device then try refreshing the page.</h3>
			<h3 ${bClass}>If this message is still shown after a few minutes then please verify public video streaming is enabled for this camera</h3>"""
	}
	else if(!state?.public_share_url) {
		d += """<h3 ${tClass}>Unable to Display Video Stream</h3><br><h3 ${bClass}>Please make sure that public video streaming is enabled at</h3><h3 ${bClass}>https://home.nest.com</h3>"""
	}
	else if(!state?.isOnline) {
		d += """<h3 ${tClass}>This Camera is Offline</h3><br><h3 ${bClass}>Please verify the camera has a Wi-Fi connection and refresh this page...</h3>"""
	}
	else {
		d += """<h3 ${tClass}>Unable to display the Live Video Stream</h3><br><br><br><h3 ${bClass}>An unknown issue has occurred...</h3><h3 ${bClass}>Please consult the Live Logs in the SmartThings IDE</h3>"""
	}
	d += """</section></div>"""
	return d
}

private def textDevName()   { return "Nest Camera${appDevName()}" }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
