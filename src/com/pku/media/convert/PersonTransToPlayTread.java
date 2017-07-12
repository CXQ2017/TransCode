/**
 * 视频分割与转码
 * Video Transcode Test Website
 */
package com.pku.media.convert;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pku.media.utils.Convertconfigure;
import com.pku.media.utils.DBManager;
import com.pku.media.utils.FFMPEG;
import com.pku.media.utils.HandleProperties;
import com.pku.media.utils.OSinfo;


/**
 * 转码
 */
public class PersonTransToPlayTread extends Thread {
	private String ffmpegPath;
	private String lastId="";
	

	public void setServletContext(String ffmpegPath) {
		this.setFfmpegPath(ffmpegPath);
	}

	// 构造函数
	public PersonTransToPlayTread(String ffmpegPath) {
		super();
		this.setFfmpegPath(ffmpegPath);
	}

	public void run() {

		String ffmpeg_path = "";
		
		String workPath=System.getProperty("user.dir");
		String confFile = workPath + "/convert.properties";
		
			// TODO Auto-generated catch block
		if (OSinfo.isWindows()) {
			ffmpeg_path = workPath + "/ffmpeg_WIN/bin/";
		} else if (OSinfo.isLinux()) {// Linux操作系统
			ffmpeg_path = workPath + "/ffmpeg_Linux/";
		}
		FFMPEG mediaTool = new FFMPEG(ffmpeg_path);
		HandleProperties propHandle = new HandleProperties(confFile);
		DBManager dbManger = new DBManager();
		ResultSet rs;
		this.lastId = propHandle.GetValueByKey("lastId");
		if(this.lastId == null){
			this.lastId = "0";
		}
		do {
			String sql = "SELECT * FROM material where id >" +this.lastId;
			
			rs = dbManger.executeQuery(sql);
			Convertconfigure config = new Convertconfigure();
			config.setTranscoderVcodec("h264");
			config.setTranscoderBv("500000");
			config.setTranscoderScaleW("400");
			config.setTranscoderScaleH("320");
			config.setTranscoderAcodec("mp3");
			config.setTranscoderKeepaspectratio("false");
			
			try {
				while (rs.next()) {
					String source = rs.getString("lowdef_video_upload_path");
					String destination = rs.getString("file_uri");
					if(source == null || destination ==null){
						continue;
					}
					this.lastId = rs.getInt("id")+"";
					if (!mediaTool.transCode(source, config, destination)) {
						continue;
					}
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally{
				try {
					propHandle.WriteProperties("lastId", this.lastId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			try {
				sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		} while (true);

	}

	public String getFfmpegPath() {
		return ffmpegPath;
	}

	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

}
