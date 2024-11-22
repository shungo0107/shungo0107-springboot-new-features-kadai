package com.example.samuraitravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository< VerificationToken, Integer> {
    public VerificationToken findByToken(String token);
}

/* リポジトリはデータベースにアクセスし、CRUD処理を行うインターフェースです。
 * インターフェース＝メソッドの名前、引数の型、戻り値の型のみを定義したもの（メソッドの具体的な処理内容は記述しない）。
 *                             複数のクラスに共通の機能を持たせたいときに使う
*/
/*リポジトリではfindBy○○○LessThanEqual()というメソッドを定義することで、SQLのWHERE句のように
 * ○○○ <= 引数の値という条件で検索できるようになります（findBy○○○LessThan()の場合は○○○ < 引数の値）。
 */
/* リポジトリではfindBy○○○And●●●()やfindBy○○○Or●●●()といったメソッドを定義することで、
 * SQLのAND検索やOR検索を行うことができます
 */