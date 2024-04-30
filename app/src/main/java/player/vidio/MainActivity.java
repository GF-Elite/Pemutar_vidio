package player.vidio;
import android.os.Bundle;
import android.widget.VideoView;
import android.app.Activity;
import android.net.Uri;
import java.io.File;
import android.os.Handler;
import android.os.Looper;
import android.media.MediaPlayer;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private String[] videoPaths;
	private int currentIndex = 0;
	private VideoView videoView;
	private Handler handler;
	private SharedPreferences sharedPreferences;
	private static final String LAST_PLAYED_VIDEO_INDEX = "last_played_video_index";
	private Button pausePlayButton;
	private boolean isPaused = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		videoView = findViewById(R.id.videoView);
		pausePlayButton = findViewById(R.id.pausePlayButton);
		handler = new Handler(Looper.getMainLooper());
		sharedPreferences = getPreferences(MODE_PRIVATE);
		
		// Ganti path direktori sesuai kebutuhan Anda
		File directory = new File("/storage/sdcard1/tiktok_ku/");
		if (directory.isDirectory()) {
			videoPaths = directory.list();
			currentIndex = sharedPreferences.getInt(LAST_PLAYED_VIDEO_INDEX, 0);
			playVideo(currentIndex);
		}
		
		pausePlayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isPaused) {
					videoView.start();
					pausePlayButton.setText("Pause");
					} else {
					videoView.pause();
					pausePlayButton.setText("Play");
				}
				isPaused = !isPaused;
			}
		});
	}
	
	private void playVideo(int index) {
		if (index < videoPaths.length) {
			String videoPath = "/storage/sdcard1/tiktok_ku/" + videoPaths[index];
			videoView.setVideoURI(Uri.parse(videoPath));
			videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					videoView.start();
				}
			});
			
			videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mediaPlayer) {
					// Play next video after current video completes
					handler.post(new Runnable() {
						@Override
						public void run() {
							int nextIndex = (index + 1) % videoPaths.length;
							playVideo(nextIndex);
						}
					});
				}
			});
			
			// Simpan indeks video terakhir yang dimainkan
			sharedPreferences.edit().putInt(LAST_PLAYED_VIDEO_INDEX, index).apply();
		}
	}
}