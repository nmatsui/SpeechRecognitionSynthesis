package jp.co.tis.tc.speechRecognitionSynthesis;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SpeechRecognitionSynthesisActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {
	private static final int REQUEST_CODE = 1;
	private EditText resultText;
	private Button speechButton;
	private RadioGroup langGroup;
	private Locale locale;
	
	private TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tts = new TextToSpeech(this, this);
        
        resultText = (EditText)findViewById(R.id.result_id);
        speechButton = (Button)findViewById(R.id.speech_id);
        speechButton.setOnClickListener(this);
        speechButton.setEnabled(false);
        langGroup = (RadioGroup)findViewById(R.id.lang_group_id);
        langGroup.check(R.id.japanese_id);
    }
    
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			speechButton.setEnabled(true);
		}
		else {
			throw new RuntimeException("Could not initialize TTS");
		}
	}
	
	@Override
	protected void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v == speechButton) {
			resultText.setText("");
			String label = "";
			switch (findViewById(langGroup.getCheckedRadioButtonId()).getId()) {
			case R.id.japanese_id:
				locale = Locale.JAPAN;
				label = "話してください";
				break;
			case R.id.english_id:
				locale = Locale.US;
				label = "Please speak";
				break;
			case R.id.chinese_id:
				locale = Locale.CHINA;
				label = "请你发言";
				break;
			default:
				throw new RuntimeException();
			}
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toString());
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, label);
			startActivityForResult(intent, REQUEST_CODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			List<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (candidates.size() > 0) {
				String text = candidates.get(0);
				resultText.setText(text);
				int result = tts.setLanguage(locale);
				if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
					tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
				}
				else {
					Toast.makeText(this, "can't speakback in this locale", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}