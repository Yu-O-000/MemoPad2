package local.hal.an25.android.memopad2.dataaccess;

import java.sql.Timestamp;

import androidx.room.TypeConverter;

/**
 * AN25 Android追加サンプル02 Room
 *
 * 日時に関するデータベース上のデータ型とJavaのデータ型を変換するクラス。
 *
 * @author Shinzo SAITO
 */
public class TimestampConverter {
	/**
	 * 日時を表すUNIXタイムスタンプのミリセカンド値(long)をJavaのTimestamp型に変換するメソッド。
	 *
	 * @param value 変換元の日時を表すUNIXタイムスタンプのミリセカンド値。
	 * @return 引数をもとに変換されたTimestampオブジェクト。
	 */
	@TypeConverter
	public static Timestamp toTimestamp(Long value) {
		Timestamp returnVal = null;
		if(value != null) {
			returnVal = new Timestamp(value);
		}
		return returnVal;
	}

	/**
	 * 日時を表すJavaのTimestampオブジェクトからUNIXタイムスタンプのミリセカンド値(long)に変換するメソッド。
	 *
	 * @param value 変換元のTimestampオブジェクト。
	 * @return 変換されたUNIXタイムスタンプのミリセカンド値。
	 */
	@TypeConverter
	public static Long toLong(Timestamp value) {
		Long returnVal = null;
		if(value != null) {
			returnVal = value.getTime();
		}
		return returnVal;
	}
}

/*
	to[Javaの型]メソッド, to[SQLite型]メソッド, それぞれ@TypeConverter で、Roomがデータ型を自動変換してくれる。
 */
