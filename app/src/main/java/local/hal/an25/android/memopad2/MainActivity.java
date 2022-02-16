package local.hal.an25.android.memopad2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import local.hal.an25.android.memopad2.dataaccess.AppDatabase;
import local.hal.an25.android.memopad2.dataaccess.Memo;
import local.hal.an25.android.memopad2.dataaccess.MemoDAO;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * AN25 Android追加サンプル02 Room
 *
 * リスト画面表示用アクティビティクラス。
 *
 * @author Shinzo SAITO
 */
public class MainActivity extends AppCompatActivity {
	/**
	 * リサイクラービューを表すフィールド。
	 */
	private RecyclerView _rvMemo;
	/**
	 * 重要メモ情報リストのみに絞り込むかどうかを表すフィールド。trueの場合は絞り込む。
	 */
	private boolean _onlyImportant = false;
	/**
	 * データベースオブジェクト。
	 */
	private AppDatabase _db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// RecyclerViewの設定
		_rvMemo = findViewById(R.id.rvMemo);
		LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
		_rvMemo.setLayoutManager(layout);
		DividerItemDecoration decoration = new DividerItemDecoration(MainActivity.this, layout.getOrientation());
		_rvMemo.addItemDecoration(decoration);

		_db = AppDatabase.getDatabase(MainActivity.this);	// コンテキストは何のために必要？
	}

	@Override
	protected void onResume() {
		super.onResume();
		createRecyclerView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_options_list, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuTitle = menu.findItem(R.id.menuTitle);
		menuTitle.setTitle(R.string.menu_list_all);
		if(_onlyImportant) {
			// 重要onlyフラグによってタイトルを書き換え
			menuTitle.setTitle(R.string.menu_list_important);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		boolean returnVal = true;

		switch(id) {
			case R.id.menuListImportant:
				_onlyImportant = true;
				invalidateOptionsMenu();
				break;
			case R.id.menuListAll:
				_onlyImportant = false;
				invalidateOptionsMenu();
				break;
			default:
				returnVal = super.onOptionsItemSelected(item);
		}
		if(returnVal) {
			createRecyclerView();
//			invalidateOptionsMenu();	RecyclerViewのSampleではこちらに配置していたが、なぜ今回は別々？
		}
		return returnVal;
	}

	/**
	 * 新規ボタンが押されたときのイベント処理用メソッド。
	 *
	 * @param view 画面部品。
	 */
	public void onNewButtonClick(View view) {
		Intent intent = new Intent(getApplicationContext(), MemoEditActivity.class);
		intent.putExtra("mode", Consts.MODE_INSERT);		// 画面レイアウトが変わるためモード識別子を受け渡し
		startActivity(intent);
	}

	/**
	 * リスト画面表示用のデータを生成するメソッド。
	 * フィールド_onlyImportantの値に合わせて生成するデータを切り替える。
	 */
	private void createRecyclerView() {
		MemoDAO memoDAO = _db.createMemoDAO();
		ListenableFuture<List<Memo>> future;		// ???
		if(_onlyImportant) {
			future = memoDAO.findAllImportant();	// 重要フラグが立っている項目のリストを取得するクエリを保存？

		} else {
			future = memoDAO.findAll();				// 全項目のリストを取得するクエリを保存？
		}

		List<Memo> memoList = new ArrayList<>();
		try {
			memoList = future.get();				// クエリを実行？

		} catch(ExecutionException ex) {
			Log.e("MainActivity", "データ取得処理失敗", ex);

		} catch(InterruptedException ex) {
			Log.e("MainActivity", "データ取得処理失敗", ex);
		}

		MemoListAdapter adapter = new MemoListAdapter(memoList);
		_rvMemo.setAdapter(adapter);
	}

	/**
	 * リサイクラービューで利用するビューホルダクラス。
	 */
	private class MemoViewHolder extends RecyclerView.ViewHolder {
		/**
		 * メモタイトル表示用TextViewフィールド。
		 */
		public TextView _tvTitleRow;
		/**
		 * 重要マーク表示用ImageViewフィールド。
		 */
		public ImageView _imStarRow;

		/**
		 * コンストラクタ。
		 *
		 * @param itemView リスト1行分の画面部品。
		 */
		public MemoViewHolder(View itemView) {
			super(itemView);
			_tvTitleRow = itemView.findViewById(R.id.tvTitleRow);
			_imStarRow = itemView.findViewById(R.id.imStarRow);
		}
	}

	/**
	 * リサイクラービューで利用するアダプタクラス。
	 */
	private class MemoListAdapter extends RecyclerView.Adapter<MemoViewHolder> {
		/**
		 * リストデータを表すフィールド。
		 */
		private List<Memo> _listData;

		/**
		 * コンストラクタ。
		 *
		 * @param listData リストデータ。
		 */
		public MemoListAdapter(List<Memo> listData) {
			_listData = listData;
		}

		@Override
		public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
			View row = inflater.inflate(R.layout.row_activity_main, parent, false);
			row.setOnClickListener(new ListItemClickListener());
			MemoViewHolder holder = new MemoViewHolder(row);
			return holder;
		}

		@Override
		public void onBindViewHolder(MemoViewHolder holder, int position) {
			Memo item = _listData.get(position);
			holder._tvTitleRow.setText(item.title);
			holder._tvTitleRow.setTag(item.id);
			holder._imStarRow.setVisibility(View.INVISIBLE);

			if(item.getImportantBool()) {
				holder._imStarRow.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public int getItemCount() {
			return _listData.size();
		}
	}

	/**
	 * リストをタップした時の処理が記述されたメンバクラス。
	 */
	private class ListItemClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			TextView tvTitleRow = view.findViewById(R.id.tvTitleRow);
			int idNo = (int) tvTitleRow.getTag();
			Intent intent = new Intent(MainActivity.this, MemoEditActivity.class);

			intent.putExtra("mode", Consts.MODE_EDIT);
			intent.putExtra("idNo", idNo);
			startActivity(intent);
		}
	}
}

/*
	新規登録時 ... mode識別子
	更新時	   ... mode識別子、要素番号
		を受け渡し
 */
