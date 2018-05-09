/********************************************************************************************
|    Application Name: NST Storage                                                      |
|    Copyright (C) 2017, 2018 Anthony S.                                                    |
|    Authors: Anthony S. (@tonesto7), Eric S. (@E_sch)                                      |
|    Contributors: Ben W. (@desertblade)                                                    |
|    A few code methods are modeled from those in CoRE by Adrian Caramaliu                  |
|                                                                                           |
|    License Info: https://github.com/tonesto7/nest-manager/blob/master/app_license.txt     |
|********************************************************************************************/
import groovy.json.JsonSlurper

definition(
	name			: "NST Storage",
    namespace		: "tonesto7",
    author			: "Anthony S.",
	description		: "NST Manger Storage Module Add-In\n\nDO NOT Install Through Marketplace\n\nThis app is Automatically installed when you open NST Manager SmartApp.",
	category		: "My Apps",
    parent			: "tonesto7:Nest Manager",
	iconUrl         : "https://echosistant.com/es5_content/images/es5_storage.png",
    iconX2Url       : "https://echosistant.com/es5_content/images/es5_storage.png",
    iconX3Url       : "https://echosistant.com/es5_content/images/es5_storage.png")
/**********************************************************************************************************************************************/
preferences { }

def appVersion() { "5.4.0" }
def appVerDate() { "05-09-2018" }
private moduleType() { return "storage" }

private installed() {
	initialize()
}

private updated() {
	log.trace "${app?.getLabel()} updated..."
	initialize()
}

private initialize() {
	log.debug "Initialized (${app.label}) | Storage Version: (${appVersion()})"
	stateCleanup()
}

def getAutomationType() { return "storage" }

def getIsAutomationDisabled() { return false }

def getStateSize()	{ return state?.toString().length() }
def getStateSizePerc()  { return (int) ((state?.toString().length() / 100000)*100).toDouble().round(0) }

def getSettingsData() {
	def sets = []
	settings?.sort().each { st ->
		sets << st
	}
	return sets
}

def getSettingVal(var) {
	return settings[var] ?: null
}

def getStateVal(var) {
	return state[var] ?: null
}

void settingUpdate(name, value, type=null) {
    log.trace("settingUpdate($name, $value, $type)...")
    if(name && type) { app?.updateSetting("$name", [type: "$type", value: value]) }
    else if (name && type == null) { app?.updateSetting(name.toString(), value) }
}

def stateUpdate(sKey, sValue) {
    if(sKey && sValue) {
		log.info "Updating State Value (${sKey}) with | ${sValue}"
        atomicState?."${sKey}" = sValue
        return true
    } else { return false }
}

def stateCleanup() {
	log.trace "stateCleanup"
	def data = [ "selectedDevMap" ]
	data.each { item ->
		state.remove(item?.toString())
	}
}

def getRoomDataById(rmId) {
	def rmData = atomicState?.stRoomMap
	return rmData?.find {it?.id} ?: null
}

def getRoomNameById(rmId) {
	def rmData = atomicState?.stRoomMap
	def room = rmData?.find {it?.id}
	return room?.id ?: null
}
