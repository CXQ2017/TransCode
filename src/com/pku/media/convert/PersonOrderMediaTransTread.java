/**
 * 视频分割与转码
 * Video Transcode Test Website
 */
package com.pku.media.convert;

import java.io.File;
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
public class PersonOrderMediaTransTread extends Thread {
	private String ffmpegPath;
	private String itemId = "";

	public void setServletContext(String ffmpegPath) {
		this.setFfmpegPath(ffmpegPath);
	}

	// 构造函数
	public PersonOrderMediaTransTread(String ffmpegPath) {
		super();
		this.setFfmpegPath(ffmpegPath);
	}

	public void run() {

		String ffmpeg_path = "";

		String workPath = System.getProperty("user.dir");
		// TODO Auto-generated catch block
		if (OSinfo.isWindows()) {
			ffmpeg_path = workPath + "/ffmpeg_WIN/bin/";
		} else if (OSinfo.isLinux()) {// Linux操作系统
			ffmpeg_path = workPath + "/ffmpeg_Linux/";
		}
		FFMPEG mediaTool = new FFMPEG(ffmpeg_path);
		String confFile = workPath + "/convert.properties";
		HandleProperties propHandle = new HandleProperties(confFile);
		String downLoadDir=propHandle.GetValueByKey("downLoadDir");
		File dirFile = new File(downLoadDir);
		if(!dirFile.isDirectory()){
			dirFile.mkdirs();
		}
		DBManager dbManger = new DBManager();
		ResultSet rs;
		do {
			String sql = "SELECT * FROM segment_to_transform where status = 0";

			rs = dbManger.executeQuery(sql);
			try {
				while (rs.next()) {
					this.itemId = rs.getInt("uuid") + "";
					int starttime = rs.getInt("starttime");
					int endtime = rs.getInt("endtime");
					String inputfile = rs.getString("highdef_video_path");
					
					String oufFmt = rs.getString("format");
					int is_entire = rs.getInt("is_entire");
					File sourceFile = new File(inputfile);
					if (!sourceFile.exists() || sourceFile.isDirectory()){
						continue;
					}
					String outFile = downLoadDir
							+ itemId + "_" + sourceFile.getName().substring(0,sourceFile.getName().lastIndexOf(".")) + "." + oufFmt;
					Convertconfigure conf = new Convertconfigure();
					if (is_entire==0) {
						conf.setStartPoint(starttime);
						conf.setEndPoint(endtime);
					}
					conf.setTranscoderOutfmt(oufFmt);
					if (!mediaTool.transCode(inputfile, conf, outFile)) {
						continue;
					}

					// 记录转码成功
					String updateSql = "UPDATE segment_to_transform SET "
							+ "order_video_path = \"" + outFile
							+ "\",status = 1 where uuid = " + itemId;
//					System.out.println(updateSql);
					dbManger.executeUpdate(updateSql);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
