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
	private static boolean musicSt = true; // ���ֶ}��
	private static final int[] musicId = { R.raw.background_music };

	// ��l�ƭ��ּ���
	public static void initMusic(Context context) {
		int r = new Random().nextInt(musicId.length);
		music = MediaPlayer.create(context, musicId[r]);
		music.setLooping(true);
	}

	/**
	 * ��o���ֶ}�����A
	 * 
	 * @return
	 */
	public static boolean isMusicSt() {
		return musicSt;
	}

	/**
	 * �]�m���ֶ}��
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
	 * ����귽
	 */
	public static void recyle() {
		if (music != null)
			music.release();
	}
}