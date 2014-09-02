package com.example.playpalpengame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class BackgroundMusicHandler {
	public static final int MUSIC_ORDINARY = 0;
	public static final int MUSIC_WIN = 1;
	public static final int MUSIC_LOSE = 2;
	
	private static MediaPlayer music;
	private static boolean musicSt = true;
	private static boolean canRecycle = true;
	private static final int[] musicId = { R.raw.background_music, R.raw.background_music_win, R.raw.background_music_win};

	public static void initMusic(Context context) {
		initMusic(context, 0);
	}
	
	public static void initMusic(Context context, int r) {
		if(canRecycle) {
			music = MediaPlayer.create(context, musicId[r]);
			music.setLooping(true);
		}
		else
			canRecycle = true;
	}

	public static boolean isMusicSt() {
		return musicSt;
	}

	public static void setMusicSt(boolean musicSt) {
		BackgroundMusicHandler.musicSt = musicSt;
		if (musicSt)
			music.start();
		else
			music.stop();
	}
	
	public static void setCanRecycle(boolean value) {
		canRecycle = value;
	}

	public static void recyle() {
		if (music != null && canRecycle)
			music.release();
	}
}