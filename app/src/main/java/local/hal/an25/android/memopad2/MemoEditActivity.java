package local.hal.an25.android.memopad2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import local.hal.an25.android.memopad2.dataaccess.AppDatabase;
import local.hal.an25.android.memopad2.dataaccess.Memo;
import local.hal.an25.android.memopad2.dataaccess.MemoDAO;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.common.util.concurrent.ListenableFuture;

import java.sql.Timestamp;
import java.util.concurrent.ExecutionException;

/**
 * AN25 Android追加サンプル02 Room
 *
 * 編集画面表示用アクティビティクラス。
 *
 * @author Shinzo SAITO
 */
public class MemoEditActivity extends AppCompatActivity {
	/**
	 * 新規登録モードか更新モードかを表すフィールド。
	 */
	private int _mode = Consts.MODE_INSERT;
	/**
	 * 更新モードの際、現在表示しているメモ情報のデータベース上の主キー値。
	 */
	private int _idNo = 0;
	/**
	 * データベースオブジェクト。
	 */
	private AppDatabase _db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_edit);

		_db = AppDatabase.getDatabase(MemoEditActivity.this);

		// モード識別子の受け取り
		Intent intent = getIntent();
		_mode = intent.getIntExtra("mode", Consts.MODE_INSERT);

		if(_mode == Consts.MODE_INSERT) {
			// 新規登録ver
			TextView tvTitleEdit = findViewById(R.id.tvTitleEdit);
			tvTitleEdit.setText(R.string.tv_title_insert);

		} else {
			// 更新ver
			_idNo = intent.getIntExtra("idNo", 0);
			MemoDAO memoDAO = _db.createMemoDAO();
			ListenableFuture<Memo> future = memoDAO.findByPK(_idNo);

			try {
				Memo memo = future.get();
				EditText etInputTitle = findViewById(R.id.etInputTitle);
				etInputTitle.setText(memo.title);
				SwitchMaterial swImportant = findViewById(R.id.swImportant);
				swImportant.setChecked(memo.getImportantBool());
				EditText etInputContent = findViewById(R.id.etInputContent);
				etInputContent.setText(memo.content);

			} catch(ExecutionException ex) {
				Log.e("MemoEditActivity", "データ取得処理失敗", ex);

			} catch(InterruptedException ex) {
				Log.e("MemoEditActivity", "データ取得処理失敗", ex);
			}
		}

		// 戻るボタン
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 場合に応じて異なるメニューを生成
	 * @param menu	Menuオブジェクト
	 * @return		true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(_mode == Consts.MODE_INSERT) {
			inflater.inflate(R.menu.menu_options_add, menu);
		}
		else {
			inflater.inflate(R.menu.menu_options_edit, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id) {
			case R.id.btnSave:		// 保存ボタン
				EditText etInputTitle = findViewById(R.id.etInputTitle);
				String inputTitle = etInputTitle.getText().toString();

				if(inputTitle.equals("")) {
					Toast.makeText(MemoEditActivity.this, R.string.msg_input_title, Toast.LENGTH_SHORT).show();

				} else {
					EditText etInputContent = findViewById(R.id.etInputContent);
					String inputContent = etInputContent.getText().toString();
					SwitchMaterial swImportant = findViewById(R.id.swImportant);	// 重要スイッチの ON/OFFを取得？
					int inputImportant = 0;

					if(swImportant.isChecked()) {
						inputImportant = 1;
					}

					Memo memo = new Memo();
					memo.title = inputTitle;
					memo.content = inputContent;
					memo.important = inputImportant;
					memo.updatedAt = new Timestamp(System.currentTimeMillis());
					MemoDAO memoDAO = _db.createMemoDAO();
					long result = 0;

					try {
						if(_mode == Consts.MODE_INSERT) {
							ListenableFuture<Long> future = memoDAO.insert(memo);
//							finish();	※ ここでfinish()を実行するとSQLが実行されたことにはならないらしい。
							result = future.get();		// SQL文の完了を同期するための処理
								/*
									get() まで行かないとSQLが実行されたことにはならないらしい。
										※ get()をせずにfinish()すると、非同期処理なのでSQL実行を待たずに次の処理が走ってしまう。
								 */

						} else {
							memo.id = _idNo;
							ListenableFuture<Integer> future = memoDAO.update(memo);
							result = future.get();
						}

					} catch(ExecutionException ex) {
						Log.e("MemoEditActivity", "データ更新処理失敗", ex);

					} catch(InterruptedException ex) {
						Log.e("MemoEditActivity", "データ更新処理失敗", ex);
					}

					if(result <= 0) {
						Toast.makeText(MemoEditActivity.this, R.string.msg_save_error, Toast.LENGTH_SHORT).show();

					} else {
						// ここを通るまで前の画面に戻らない
						finish();
					}
				}
				return true;

			case R.id.btnDelete:		// 削除ボタン
				DeleteConfirmDialogFragment dialog = new DeleteConfirmDialogFragment(_db);
				Bundle extras = new Bundle();
				extras.putInt("id", _idNo);
				dialog.setArguments(extras);
				FragmentManager manager = getSupportFragmentManager();
				dialog.show(manager, "DeleteConfirmDialogFragment");
				return true;

			case android.R.id.home:		// 戻るボタン（android.R.id.homeは共通のR値）
				finish();

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
