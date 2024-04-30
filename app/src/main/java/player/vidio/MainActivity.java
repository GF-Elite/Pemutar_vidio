package player.vidio;
import android.os.Bundle;
import android.widget.VideoView;
import android.app.Activity;
import android.net.Uri;
import java.io.File;
import android.os.Handler;
import android.os.Looper;
import android.media.MediaPlayer;

public class MainActivity extends Activity {
	
	private String[] videoPaths;
	private int currentIndex = 0;
	private VideoView videoView;
	private Handler handler;
	private int currentPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		videoView = findViewById(R.id.videoView);
		handler = new Handler(Looper.getMainLooper());
		
		// Ganti path direktori sesuai kebutuhan Anda
		File directory = new File("/storage/sdcard1/tiktok_ku/");
		if (directory.isDirectory()) {
			videoPaths = directory.list();
			// Cek apakah ada posisi video yang disimpan
			if (savedInstanceState != null) {
				currentPosition = savedInstanceState.getInt("currentPosition");
			}
			playNextVideo();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Lanjutkan video dari posisi yang disimpan
		if (videoView != null && currentPosition > 0) {
			videoView.seekTo(currentPosition);
			videoView.start();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Simpan posisi video saat keluar dari aplikasi
		if (videoView != null) {
			currentPosition = videoView.getCurrentPosition();
			videoView.pause();
		}
	}
	
	private void playNextVideo() {
		if (currentIndex < videoPaths.length) {
			String videoPath = "/storage/sdcard1/tiktok_ku/" + videoPaths[currentIndex];
			videoView.setVideoURI(Uri.parse(videoPath));
			videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					// Set posisi video dari posisi yang disimpan
					mediaPlayer.seekTo(currentPosition);
					mediaPlayer.start();
				}
			});
			currentIndex++;
			
			videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mediaPlayer) {
					// Play next video after current video completes
					handler.post(new Runnable() {
						@Override
						public void run() {
							playNextVideo();
						}
					});
				}
			});
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Simpan posisi video saat perubahan konfigurasi (misalnya rotasi layar)
		outState.putInt("currentPosition", currentPosition);
	}
}