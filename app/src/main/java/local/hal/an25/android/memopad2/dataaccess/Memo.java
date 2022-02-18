package local.hal.an25.android.memopad2.dataaccess;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * AN25 Android追加サンプル02 Room
 *
 * メモ情報のエンティティクラス。
 *
 * @author Shinzo SAITO
 */
@Entity
public class Memo {
	/**
	 * 主キーのid。
	 */
	@PrimaryKey(autoGenerate = true)
	public int id;
	/**
	 * タイトル。
	 */
	@NonNull
	public String title;
	/**
	 * 内容。
	 */
	public String content;
	/**
	 * 重要フラグ。
	 * 1=ON、0=OFF。
	 */
	@NonNull
	@ColumnInfo(defaultValue = "0")
	public int important;
	/**
	 * 更新日時。
	 */
	@NonNull
	public Timestamp updatedAt;

	/**
	 * 重要フラグのBool値を取得するメソッド。
	 *
	 * @return 重要な場合はtrue、そうでない場合はfalse。
	 */
	public boolean getImportantBool() {
		boolean importantBool = false;
		if(important == 1) {
			importantBool = true;
		}
		return importantBool;
	}
}

/*

	@Entity
		... Databaseのエンティティとして使用するクラスにつける。このEntityをテーブルとして作成。
			※ @Entity(tableName = "テーブル名") でEntityクラス名とは別のテーブル名を指定できる。

	@PrimaryKey(autoGenerate = true)
		... DBのプライマリーキー。autoGenerateはオートインクリメントを表す

	@NotNull
		... DBの NOT NULL

	@ColumnInfo(defaultValue = "0")
		... カラムに対するオプションを定義
			※ (name = "カラム名") で変数名と別名のカラム名を指定

	データ型はJavaと対応。
		※ Date型も存在しないので、Timestampは変換が必要。
 */
