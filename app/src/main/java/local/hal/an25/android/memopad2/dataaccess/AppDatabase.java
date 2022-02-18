package local.hal.an25.android.memopad2.dataaccess;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * AN25 Android追加サンプル02 Room
 *
 * データベースオブジェクトクラス。
 *
 * @author Shinzo SAITO
 */
@Database(entities = {Memo.class}, version=1)
@TypeConverters({TimestampConverter.class})			// 型変換用のクラスをアノテーションで指定
public abstract class AppDatabase extends RoomDatabase {
	/**
	 * データベースインスタンス。
	 */
	private static AppDatabase _instance;

	/**
	 * データベースインスタンスを得るメソッド。
	 * シングルトンパターンに従ってインスタンスを生成する。
	 *
	 * @param context コンテキスト。
	 * @return データベースインスタンス。
	 */
	public static AppDatabase getDatabase(Context context) {
		if (_instance == null) {
			_instance = Room.databaseBuilder
					(context.getApplicationContext(), AppDatabase.class, "memo_db").build();
				/*
					・context.getApplicationContext() ... activityのコンテキストではなく、アプリケーション全体のコンテキスト。
					・AppDatabase.class ... 自分自身
					・"memo_db" ... db名
				 */
		}
		return _instance;
	}

	/**
	 * MemoDAOオブジェクトを生成するメソッド。
	 *
	 * @return MemoDAOオブジェクト。
	 */
	public abstract MemoDAO createMemoDAO();
		/*
			中身は自動生成される。（メソッド名は自由、戻り値は必ず〇〇DAOなので、そこで判定してる？）
		 */
}

/*
	抽象クラスにインスタンスを保持するSingletonパターン

	Room
		... DBからDAOまでの非同期処理を生成するためのライブラリ
			※ build.gradle >> dependencies に書いて読み込む。

			※ Roomはオブジェクト自体をDBとしてデータを保持？
				Q. Singletonのインスタンスに格納しているのだとしたら、Buildしなおしたら消えるのでは？
					A. 最終的にSQLite上に保存してるので問題なし。

	@Database(entities = {エンティティクラス}, version=1)
		... RoomDatabaseを継承したクラスにつける。
			※ エンティティクラスには @Entity をつける。
			※ DAOインターフェースには @Dao をつける。

	Javaの代入
		・プリミティブ型は実態を複製
		・オブジェクト型は参照先を代入

	Javaのトランザクション
		・SQL上でトランザクションの処理を書くこともできるが、Javaでもできる。
			DAOと合わせるので、Java側でやったほうがいいかも。
 */
