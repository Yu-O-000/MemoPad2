package local.hal.an25.android.memopad2;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import local.hal.an25.android.memopad2.dataaccess.AppDatabase;
import local.hal.an25.android.memopad2.dataaccess.Memo;
import local.hal.an25.android.memopad2.dataaccess.MemoDAO;

/**
 * AN25 Android追加サンプル02 Room
 *
 * 削除確認ダイアログクラス。
 *
 * @author Shinzo SAITO
 */
public class DeleteConfirmDialogFragment extends DialogFragment {
	/**
	 * データベースオブジェクト。
	 */
	private AppDatabase _db;

	/**
	 * コンストラクタ。
	 *
	 * @param db データベースオブジェクト。
	 */
	public DeleteConfirmDialogFragment(AppDatabase db) {
		_db = db;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.del_dialog_title);
		builder.setMessage(R.string.del_dialog_message);
		builder.setPositiveButton(R.string.del_dialog_positive, new DeleteConfirmDialogClickListener());
		builder.setNegativeButton(R.string.del_dialog_negative, new DeleteConfirmDialogClickListener());
		AlertDialog dialog = builder.create();
		return dialog;
	}

	/**
	 * 削除確認ダイアログのボタンが押されたときの処理が記述されたメンバクラス。
	 */
	private class DeleteConfirmDialogClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which ==  DialogInterface.BUTTON_POSITIVE) {
				Activity parent = getActivity();
				Bundle extras = getArguments();
				int idNo = extras.getInt("id", 0);
				Memo memo = new Memo();
				memo.id = idNo;
				MemoDAO memoDAO = _db.createMemoDAO();
				int result = 0;
				ListenableFuture<Integer> future = memoDAO.delete(memo);
				try {
					result = future.get();
				}
				catch(ExecutionException ex) {
					Log.e("DeleteConfirmDialog", "データ更新処理失敗", ex);
				}
				catch(InterruptedException ex) {
					Log.e("DeleteConfirmDialog", "データ更新処理失敗", ex);
				}
				if(result <= 0) {
					Toast.makeText(parent, R.string.msg_delete_error, Toast.LENGTH_SHORT).show();
				}
				else {
					parent.finish();
				}
			}
		}
	}
}
