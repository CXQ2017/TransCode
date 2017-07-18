/**
 * 视频分割与转码
 * Video Transcode Test Website
 */
package com.pku.media.convert;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pku.media.utils.DBManager;
import com.pku.media.utils.FFMPEG;
import com.pku.media.utils.HandleProperties;
import com.pku.media.utils.OSinfo;


/**
 * 转码
 */
public class EnterpriseTransToPlayTread extends Thread {
	private String ffmpegPath;
	private String lastId="";
	

	public void setServletContext(String ffmpegPath) {
		this.setFfmpegPath(ffmpegPath);
	}

	// 构造函数
	public EnterpriseTransToPlayTread(String ffmpegPath) {
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
//		this.lastId = propHandle.GetValueByKey("lastId");
//		if(this.lastId == null){
//			this.lastId = "0";
//		}
		do {
			//根据trans_status转码
			String sql = "SELECT * FROM upload_log where material_id=-1 and play_status = 0";
			//material_id = -1 表示是企业上传媒资
			rs = dbManger.executeQuery(sql);
			try {
				while (rs.next()) {
					this.lastId = rs.getInt("log_id")+"";
					String source = rs.getString("video_upload_path");
					String destination = rs.getString("video_play_path");
					if (!mediaTool.convertContainer(source, destination)) {
						continue;
					}
					
					String updateSql = "UPDATE upload_log SET "
							+  "play_status = 1 where log_id = " + lastId;
//					System.out.println(updateSql);
					dbManger.executeUpdate(updateSql);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			finally{
//				try {
//					propHandle.WriteProperties("lastId", this.lastId);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
			
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
