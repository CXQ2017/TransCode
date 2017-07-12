package com.pku.media.convert;

import com.pku.media.utils.OSinfo;

public class EnterprisePlayConvertService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String workPath = System.getProperty("user.dir");
		String ffmpegPath="";
		if (OSinfo.isWindows()) {
			ffmpegPath = workPath + "/ffmpeg_WIN/bin/";
		} else {
			ffmpegPath = workPath + "/ffmpeg_Linux/";
		}


		// // 初始化的时候运行以下几个线程
		// // 截图
		// VideoThumbnailThread2 videoThumbnailThread = new
		// VideoThumbnailThread2(sc);
		// videoThumbnailThread.start();

		// 企业上传转码成网页播放格式
		System.out.print("Starting Enterprise Playing Transcoding Thread...\n");
		EnterpriseTransToPlayTread eprTransToPlayTread = new EnterpriseTransToPlayTread(ffmpegPath);
		eprTransToPlayTread.start();
		
		// 个人上传转码成网页播放格式
//		PersonTransToPlayTread transToPlayTread = new PersonTransToPlayTread(ffmpegPath);
//		transToPlayTread.start();

		// 个人上传转码成网页播放格式
//		PersonOrderMediaTransTread transTread = new PersonOrderMediaTransTread(ffmpegPath);
//		transTread.start();
		
		//企业订单转码
//		EnterpriseOrderMediaTransTread orderMediaTransTread = new EnterpriseOrderMediaTransTread(ffmpegPath);
//		orderMediaTransTread.start();
	}

}
