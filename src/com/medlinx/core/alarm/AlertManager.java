package com.medlinx.core.alarm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;

/**
 * 报警管理类，负责每个病人的报警管理。更新监控的生理和技术指标后给出报警状态。需要维护目前的状态（禁止暂停静音和暂时静音）.
 * 
 * @author bruce.bei
 * 
 */
public class AlertManager {
    
    private MetaInformation information;//病人信息，心率、电池余量等
    private SettingParameters parameters;//设置参数，告警阀值
    private ArrayList<Alert> list;//告警列表
    private int level = 1;//告警等级
    private Boolean cancelFlag,cancelTempFlag,banBmpFlag,banBatteryRemain;//静音,暂时静音,禁止心率报警，禁止电池余量报警
    private Timer countDownMuteTimer,countDownCancelTimer;
    private long countDownMute,countDownCancel;//暂时静音和暂时禁止倒计时
    private CountDownMuteTask countDownMuteTask;//暂时静音监听器
    private CountDownCancelTask countDownCancelTask;//暂时禁止监听器
    private Alert alert;
    private Map<Integer,Boolean> map;
    
    public long getCountDownMute() {
        return countDownMute;
    }

    public void setCountDownMute(long countDownMute) {
        this.countDownMute = countDownMute;
    }

    public long getCountDownCancel() {
        return countDownCancel;
    }

    public void setCountDownCancel(long countDownCancel) {
        this.countDownCancel = countDownCancel;
    }
    
    public ArrayList<Alert> getList() {
        return list;
    }

    public void setList(ArrayList<Alert> list) {
        this.list = list;
    }

    public Map<Integer, Boolean> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Boolean> map) {
        this.map = map;
    }

    /**
     * 设置阀值参数等
     */
    public void setSettingParameters(SettingParameters settingParameters){
        parameters = settingParameters;
    }
    
    /**
     * 设置病人的信息，实时更新病人各项指标。
     * 
     * @param information
     */
    public void setMetaInformation(MetaInformation information) {
        this.information = information;
    }

    /**
     * 禁止某项报警.
     * 
     * @param alert
     */
    public void banAlert(Alert alert) {
        if(alert.getLevel()==1){
            banBmpFlag=true;
        }else if(alert.getLevel()==2){
            banBatteryRemain=true;
        }
    }

    /**
     * 暂时禁止某项报警,milliseconds是禁止的时间长度（如一分钟或者半分钟）。
     * 
     * @param alert
     * @param milliseconds
     */
    public void tempBanAlert(Alert alert, long milliseconds) {
        this.alert = alert;
        banAlert(alert);
        countDownCancel = milliseconds/1000;
        if(countDownCancel == 0){
            if(countDownCancelTimer.isRunning())
                countDownCancelTimer.stop();
            countDownCancel = 0;
        }else{
            countDownCancelTask = new CountDownCancelTask();
            countDownCancelTimer = new Timer(1000,countDownCancelTask);
            countDownCancelTimer.start();
        }
    }

    /**
     * 彻底静音
     */
    public void mute() {
        cancelFlag = true;
    }

    /**
     * 暂时静音，参数是暂时静音的时长。
     * 
     * @param millisenconds
     */
    public void tempMute(long millisenconds) {
        countDownMute = millisenconds/1000;
        if(countDownMute == 0){
            if(countDownMuteTimer.isRunning())
                countDownMuteTimer.stop();
            countDownMute = 0;
            cancelTempFlag = false;
        }else{
            cancelTempFlag = true;
            countDownMuteTask = new CountDownMuteTask();
            countDownMuteTimer = new Timer(1000,countDownMuteTask);
            if(!countDownMuteTimer.isRunning())
                countDownMuteTimer.start();
            list = getAlerts();
            map = new HashMap<Integer,Boolean>();
            for(Alert alert : list){
                map.put(alert.getLevel(), cancelTempFlag);
            }
        }
    }

    /**
     * 返回当前报警的级别。用于控制声音报警的级别。注意有多个报警有效的情况下返回最高的级别。
     * 
     * @return
     */
    public synchronized int getAlertLevel() {
        list = getAlerts();
        //int level = 1;
        if(cancelFlag !=null && cancelFlag == true){
            return level; 
        }
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Alert alert = (Alert) iterator.next();
			if(map!=null){
                if(!map.containsKey(alert.getLevel()))
                    return alert.getLevel();
                else {
                    if(map.get(alert.getLevel())==false&&level>alert.getLevel()){
                        level=alert.getLevel();
                    }
                }
            } else if(level>alert.getLevel()){
                level=alert.getLevel();
            }
		}
        return level;
        
    }
    
    /**
     * 返回当前所有被激活的报警。只有受禁止和暂时禁止影响
     * 
     * @return
     */
    public ArrayList<Alert> getAlerts() {
        list = new ArrayList<Alert>();
        int bpmTemp = information.getHeartbeat();
        int batteryRemainTemp = information.getBatteryRemain();
        
        //心率开关是否已开启
        if(parameters.isBpmAlarmOn()){
            //心率报警是否被禁止或者暂时被禁止，
            if(banBmpFlag==null||!banBmpFlag&&countDownCancel==0){
                Alert alert = new Alert();
                alert.setAlertType("生理报警");
                alert.setLevel(1);
                //心率过低报警
                if(bpmTemp<parameters.getHrMin()){
                    alert.setAlertMessage("心率过低");
                    list.add(alert);
                } else if(bpmTemp>parameters.getHrMax()){
                    //心率过高报警
                    alert.setAlertMessage("心率过高");
                    list.add(alert);
                }
            }
        }
        
        //电池余量报警开关是否已开启
        if(parameters.isBatteryAlarmOn()){
            //电池余量报警是否被禁止或者暂时被禁止，
            if(banBatteryRemain==null||!banBatteryRemain&&countDownCancel==0){
                Alert alert = new Alert();
                alert.setAlertType("技术报警");
                alert.setLevel(2);
                //电池余量报警
                if(batteryRemainTemp<parameters.getBatteryAlarmBoundary()){
                    alert.setAlertMessage("电池余量不足");
                    list.add(alert);
                } 
            }
        }
        return list;
    }
    
    
    private class CountDownMuteTask implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            --countDownMute;
            if (countDownMute < 0){
                countDownMute = 0;
                cancelTempFlag = false;
                list = getAlerts();
                map = new HashMap<Integer,Boolean>();
                for(Alert l : list){
                    map.put(l.getLevel(), cancelTempFlag);
                }
            }
        }
    }
    
    private class CountDownCancelTask implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            --countDownCancel;
            if (countDownCancel < 0){
                countDownCancel = 0;
                if(alert.getLevel()==1){
                    banBmpFlag=false;
                }else if(alert.getLevel()==2){
                    banBatteryRemain=false;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        //病人信息和设备信息
        MetaInformation information = new MetaInformation();
        information.setHeartbeat(20);
        information.setBatteryRemain(10);
        //设置参数
        SettingParameters parameters = new SettingParameters();
        //告警管理参数
        AlertManager a = new AlertManager();
        a.setMetaInformation(information);
        a.setSettingParameters(parameters);
        
        Alert alert = new Alert();
        alert.setLevel(2);
        
        //测试禁止
//        a.banAlert(alert);
        
        //测试暂时禁止
//        a.tempBanAlert(alert, 2000);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        
        //测试静音
//        a.mute();
        
        //测试暂时静音
        a.tempMute(1000);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //输出信息
        List<Alert> list = a.getAlerts();
        for(Alert l : list){
            DebugTool.printLogDebug(l.getAlertMessage());
        }
        DebugTool.printLogDebug(a.getAlertLevel());
    
    }
}
