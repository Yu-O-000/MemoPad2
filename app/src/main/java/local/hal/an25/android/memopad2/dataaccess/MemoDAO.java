package local.hal.an25.android.memopad2.dataaccess;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * AN25 Android追加サンプル02 Room
 *
 * メモ情報を操作するDAOインターフェース。
 *
 * @author Shinzo SAITO
 */
@Dao
public interface MemoDAO {
	/**
	 * 全データ検索メソッド。
	 *
	 * @return 検索結果のListオブジェクトに関連するLiveDataオブジェクト。
	 */
	@Query("SELECT * FROM memo ORDER BY updatedAt DESC")
	ListenableFuture<List<Memo>> findAll();

	/**
	 * 重要メモデータ検索メソッド。
	 *
	 * @return 検索結果のListオブジェクトに関連するLiveDataオブジェクト。
	 */
	@Query("SELECT * FROM memo WHERE important = 1 ORDER BY updatedAt DESC")
	ListenableFuture<List<Memo>> findAllImportant();

	/**
	 * 主キーによる検索。
	 *
	 * @param id 主キー値。
	 * @return 主キーに対応するデータを格納したMemoオブジェクトに関連するListenableFutureオブジェクト。
	 */
	@Query("SELECT * FROM memo WHERE id = :id")
	ListenableFuture<Memo> findByPK(int id);

	/**
	 * メモ情報を新規登録するメソッド。
	 *
	 * @param memo 登録メモ情報が格納されたMemoオブジェクト。
	 * @return 新規登録された主キー値に関連するListenableFutureオブジェクト。
	 */
	@Insert
	ListenableFuture<Long> insert(Memo memo);

	/**
	 * メモ情報を更新するメソッド。
	 *
	 * @param memo 更新メモ情報が格納されたMemoオブジェクト。
	 * @return 更新件数を表す値に関連するListenableFutureオブジェクト。
	 */
	@Update
	ListenableFuture<Integer> update(Memo memo);

	/**
	 * メモ情報を削除するメソッド。
	 *
	 * @param memo 削除メモ情報が格納されたMemoオブジェクト。
	 * @return 削除件数を表す値に関連するListenableFutureオブジェクト。
	 */
	@Delete
	ListenableFuture<Integer> delete(Memo memo);
}
