package jp.techacademy.takehito.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import android.util.Log
import androidx.appcompat.app.AlertDialog
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import jp.techacademy.takehito.apiapp.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity(), FragmentCallback {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //ここまでカリキュラムで作成

        /*
        課題用追加
         */
        val address = intent.getStringExtra("address").toString()
        binding.webAddressTextView.text = address
        Log.d("address---------------------------------------", address)

        val id = intent.getStringExtra("id").toString()!!

        val logoImage = intent.getStringExtra("image").toString()
        Picasso.get().load(logoImage).into(binding.webImageView)

        val name = intent.getStringExtra("name").toString()
        binding.webNameTextView.text = name
        Log.d("name---------------------------------------", name)

        val url = intent.getStringExtra(KEY_URL).toString()
        binding.webView.loadUrl(url)

        /*
        ApiAdapter参照
         */
        //お気に入りの処理
        binding.webFavoriteImageView.apply {

            val couponUrls =
                CouponUrls(intent.getStringExtra(KEY_URL)!!, intent.getStringExtra(KEY_URL)!!)
            val shop = Shop(couponUrls, address, id, logoImage, name)

            val id = intent.getStringExtra("id").toString()
            val isFavorite = FavoriteShop.findBy(id) != null

            //星を設定
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
            //星をタップしたときの処理
            setOnClickListener {
                if (isFavorite(id)) {
                    onDeleteFavorite(id)
                    setImageResource(R.drawable.ic_star_border)
                } else {
                    onAddFavorite(shop)
                    setImageResource(R.drawable.ic_star)
                }
            }
        }
    }

    /*
    以下課題用追加
     */
    //MainActivity参照
    private fun isFavorite(id: String): Boolean {
        return FavoriteShop.findBy(id) != null
    }

    override fun onClickItem(shop: Shop) {
        TODO("Not yet implemented")
    }

    override fun onAddFavorite(shop: Shop) {
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
            address = shop.address
            url = shop.couponUrls.sp.ifEmpty { shop.couponUrls.pc }
        })
    }

    override fun onDeleteFavorite(id: String) {
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {

        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .show()
    }

    private fun deleteFavorite() {
        FavoriteShop.delete(intent.getStringExtra(ID)!!)
    }

    /*
    課題用修正　カリキュラム作成を修正
     */
    companion object {
        private const val KEY_URL = "key_url"
        private const val ID = "id"
        private const val ADDRESS = "address"
        private const val NAME = "name"
        private const val IMAGE = "image"

        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java).putExtra(
                    KEY_URL,
                    shop.couponUrls.sp.ifEmpty { shop.couponUrls.pc }
                ).putExtra(
                    NAME, shop.name
                ).putExtra(
                    IMAGE, shop.logoImage
                ).putExtra(
                    ADDRESS, shop.address
                ).putExtra(
                    ID, shop.id
                )
            )
        }
    }
}
