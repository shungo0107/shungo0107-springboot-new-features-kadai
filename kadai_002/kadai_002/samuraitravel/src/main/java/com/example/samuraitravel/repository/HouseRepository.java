package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;

public interface HouseRepository extends  JpaRepository<House, Integer>{
	// Like検索を行う
	public Page<House> findByNameLike(String keyword, Pageable pageable);
	
    public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);  
    public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);  
    public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
    public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
    public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable); 
    public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<House> findAllByOrderByPriceAsc(Pageable pageable);   
    
    public List<House> findTop10ByOrderByCreatedAtDesc();
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