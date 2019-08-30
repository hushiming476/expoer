package cn.itcast.web.task;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 任务类：cn.itcast.web.task.MyTask
 * 任务方法： execute()
 */
public class MyTask {
    // 定时执行的方法。定时？在spring整合quartz的配置文件中配置
    public void execute(){
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
