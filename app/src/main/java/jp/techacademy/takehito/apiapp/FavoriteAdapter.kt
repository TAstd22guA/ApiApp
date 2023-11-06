package jp.techacademy.takehito.apiapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.techacademy.takehito.apiapp.databinding.RecyclerFavoriteBinding

class FavoriteAdapter : ListAdapter<FavoriteShop, FavoriteItemViewHolder>(FavoriteCallback()) {

    // お気に入り画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((FavoriteShop) -> Unit)? = null

    // Itemを押したときのメソッド
    var onClickItem: ((Shop) -> Unit)? = null

    /**
     * ViewHolderを生成して返す
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        // ViewBindingを引数にApiItemViewHolderを生成
        val view =
            RecyclerFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteItemViewHolder(view)
    }

    /**
     * 指定された位置（position）のViewにFavoriteShopの情報をセットする
     */
    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        holder.bind(getItem(position), position, this)
    }
}

/**
 * お気に入りが登録されているときのリスト
 */
class FavoriteItemViewHolder(private val binding: RecyclerFavoriteBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(favoriteShop: FavoriteShop, position: Int, adapter: FavoriteAdapter) {

        /*
        課題用追加　お気に入り登録の店の項目を取得
         */
        val couponUrls = CouponUrls(favoriteShop.url, favoriteShop.url)
        val shop = Shop(
            couponUrls,
            favoriteShop.address,
            favoriteShop.id,
            favoriteShop.imageUrl,
            favoriteShop.name
        )

        // 偶数番目と奇数番目で背景色を変更させる
        binding.rootView.apply {
            binding.rootView.setBackgroundColor(
                ContextCompat.getColor(
                    binding.rootView.context,
                    if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray
                )
            )
            // クリック時のイベントリスナーを割り当て
            setOnClickListener {
                adapter.onClickItem?.invoke(shop)
            }
        }

        // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
        binding.nameTextView.text = favoriteShop.name
        binding.addressTextView.text = favoriteShop.address
        /*
        課題追加用　住所（address）を追加
         */

        // Picassoというライブラリを使ってImageVIewに画像をはめ込む
        Picasso.get().load(favoriteShop.imageUrl).into(binding.imageView)

        // 星をタップした時の処理
        // ※レイアウトの星のアイコンは既定で塗りつぶしなので設定不要
        binding.favoriteImageView.setOnClickListener {
            adapter.onClickDeleteFavorite?.invoke(favoriteShop)
            adapter.notifyItemChanged(position)
        }
    }
}

/**
 * データの差分を確認するクラス
 */
internal class FavoriteCallback : DiffUtil.ItemCallback<FavoriteShop>() {

    override fun areItemsTheSame(oldItem: FavoriteShop, newItem: FavoriteShop): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoriteShop, newItem: FavoriteShop): Boolean {
        return oldItem.equals(newItem)
    }
}