package com.example.playpalpengame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class BackgroundMusicHandler {

	private static MediaPlayer music;
	private static boolean musicSt = true; // 音樂開關
	private static final int[] musicId = { R.raw.background_music };

	// 初始化音樂播放器
	public static void initMusic(Context context) {
		int r = new Random().nextInt(musicId.length);
		music = MediaPlayer.create(context, musicId[r]);
		music.setLooping(true);
	}

	/**
	 * 獲得音樂開關狀態
	 * 
	 * @return
	 */
	public static boolean isMusicSt() {
		return musicSt;
	}

	/**
	 * 設置音樂開關
	 * 
	 * @param musicSt
	 */
	public static void setMusicSt(boolean musicSt) {
		BackgroundMusicHandler.musicSt = musicSt;
		if (musicSt)
			music.start();
		else
			music.stop();
	}

	/**
	 * 釋放資源
	 */
	public static void recyle() {
		if (music != null)
			music.release();
	}
}