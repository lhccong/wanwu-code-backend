package com.cong.wanwu.usercenter.job.cycle;

import cn.hutool.core.date.CalendarUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cong.wanwu.usercenter.model.vo.WxLoveData;
import com.cong.wanwu.usercenter.model.vo.WxLoveMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * INC同步发送wx爱信息
 *
 * @author 聪
 * @date 2023/10/23
 */
// todo 取消注释开启任务
@Component
public class IncSyncSendWxLoveMessage {

    @Value("${wx.test.appId}")
    private String appId;
    @Value("${wx.test.secret}")
    private String secret;
    @Value("${wx.test.toUser}")
    private String toUser;
    @Value("${wx.test.templateId}")
    private String templateId;
    @Value("${wx.test.weatherKeyLocation}")
    private String weatherKeyLocation;
    @Value("${wx.test.qweatherKey}")
    private String qweatherKey;


    /**
     * 每分钟执行一次
     * 早上七点半，每天执行一次
     */
    @Scheduled(cron = "0 30 7 * * ?")
    public void run() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+secret;

        String tokenStr = HttpUtil.get(url);
        //数据转换获取Token
        Map<String,String> tokenMap = JSONUtil.toBean(tokenStr, Map.class);
        //Token参数
        String accessToken = tokenMap.get("access_token");
        String weatherUrl = "https://devapi.qweather.com/v7/weather/3d?location="+weatherKeyLocation+"&key="+qweatherKey;
        //获取当前天气
        String weatherStr = HttpUtil.get(weatherUrl);
        //获取当前天气json
        Map<String,Object> weatherMap = JSONUtil.toBean(weatherStr, Map.class);
        List<Map<String,String>> weatherList = (List<Map<String,String>>) weatherMap.get("daily");
        Map<String,String> resWeather = weatherList.get(0);
        //今天日期
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDate = today.format(formatter);
        //计算生日日期
        long daysUntilBirthday = getRemainDay("12-04");
        //计算周几
        String todayofSeven = getTodayofSeven();
        //推送消息
        String urlSendMsg = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
        WxLoveMessage wxLoveMessage = new WxLoveMessage();
        wxLoveMessage.setTouser(toUser);
        wxLoveMessage.setTemplate_id(templateId);
        wxLoveMessage.setUrl("https://time.is/");
        Map<String, WxLoveData> data = wxLoveMessage.getData();
        //今天日期
        addLoveMessage("date",formattedDate,"#173177",data);
        addLoveMessage("startText",todayofSeven,"#173177",data);
        addLoveMessage("city","珠海金湾","#173177",data);
        addLoveMessage("textDay",resWeather.get("textDay"),"#173177",data);
        addLoveMessage("tempMin",resWeather.get("tempMin")+ '℃',"#1d1dff",data);
        addLoveMessage("tempMax",resWeather.get("tempMax")+ '℃',"#ff1d1d",data);
        addLoveMessage("windScaleDay",resWeather.get("windScaleDay")+ '级',"#8B4513",data);
        addLoveMessage("windScaleNight",resWeather.get("windScaleNight")+ '级',"#8B4513",data);
        addLoveMessage("love_day",withDays(2022,2,14),"#FF69B4",data);
        addLoveMessage("birthday_day", String.valueOf(daysUntilBirthday),"#FF69B4",data);
        String post = HttpUtil.post(urlSendMsg, JSONUtil.toJsonStr(wxLoveMessage));
        System.out.println(post);
    }
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(CalendarUtil.class);
    private static final Integer DAY_TIME_IN_MILLIS = 86400000; // 1000 * 24 * 60 * 60

    public static String withDays(int year, int month, int day){
        LocalDate startDate = LocalDate.of(year, month, day); //假设你们是从2023年1月1日开始的
        LocalDate currentDate = LocalDate.now(); //获取当前日期

        // 计算差值，结果是一个Duration对象，可以使用单位进行运算
        long days = ChronoUnit.DAYS.between(startDate, currentDate);

        // 计算情侣在一起的天数
        return String.valueOf(days);
    }

    public static Long getRemainDay(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date now = new Date();
        String year = sdf.format(now);
        String dateStr = year + "-" + date;
        SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date time;
        try {
            time = parseSdf.parse(dateStr);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        long diff = time.getTime() - now.getTime();
        long day = diff / DAY_TIME_IN_MILLIS;
        if (day < 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            calendar.add(Calendar.YEAR, 1);
            diff = calendar.getTimeInMillis() - now.getTime();
            day = diff / DAY_TIME_IN_MILLIS;
        }
        return day;
    }

    public static void addLoveMessage(String key,String value,String color,Map<String, WxLoveData> data){
        WxLoveData wxLoveData = new WxLoveData();
        wxLoveData.setValue(value);
        wxLoveData.setColor(color);
        data.put(key,wxLoveData);
    }

    public static String getTodayofSeven() {
        // 转换为Java中的Date对象
        Date now = new Date();
        // 使用Java中的方法获取星期几
        int day = now.getDay();
        String text = "";
        System.out.println("模板消息推送成功 day = " + day);
        switch (day) {
            case 1:
                text = "宝贝周一了给我打起精神来，爱你！";
                break;
            case 2:
                text = "周二了宝宝！吃点什么好捏";
                break;
            case 3:
                text = "周三了小宝--昨日入城市，归来泪满巾";
                break;
            case 4:
                text = "宝呗今天疯狂星期四，V我50";
                break;
            case 5:
                text = "md周五了！，食餐劲！";
                break;
            case 6:
                text = "走啊，周六，出去玩了宝贝";
                break;
            case 0:
                text = "哎呀怎么又周末了，赶快抓紧时间玩！";
                break;
        }
        return text;
    }

}
