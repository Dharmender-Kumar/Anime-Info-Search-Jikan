package com.deku.animesearchjikan

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deku.animesearchjikan.databinding.ActivityMainBinding
import com.deku.animesearchjikan.services.AnimeService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            val animeService = AnimeService.create()
            val call = animeService.getTopAnime()

            call.enqueue(object : Callback<TopAnime>{
                override fun onResponse(call: Call<TopAnime>, response: Response<TopAnime>) {
                    if(response.body() != null){
                        val top =response.body()!!.top
                         animeRecyclerView.adapter = AnimeAdapter(this@MainActivity, top )
                        animeRecyclerView.layoutManager= GridLayoutManager(this@MainActivity,3)
                    }
                }

                override fun onFailure(call: Call<TopAnime>, t: Throwable) {
                }

            })

            searchButton.setOnClickListener{

                val searchedAnime = searchInputEditText.text.toString()
                val callSearachedAnime = animeService.getSearchedAnime(searchedAnime)
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(searchInputEditText.getWindowToken(), 0)

                callSearachedAnime.enqueue(object : Callback<SearchedAnime>{
                    override fun onResponse(
                        call: Call<SearchedAnime>,
                        response: Response<SearchedAnime>
                    ) {
                        if(response.body()!=null){
                            val searchedAnime = response.body()!!.results
                            animeRecyclerView.adapter = AnimeAdapter(this@MainActivity, searchedAnime )
                            animeRecyclerView.layoutManager= GridLayoutManager(this@MainActivity,3)
                        }
                    }

                    override fun onFailure(call: Call<SearchedAnime>, t: Throwable) {

                    }

                })

            }
        }
    }

    class AnimeAdapter(
        private val parentActivity: AppCompatActivity,
                private val animes:List<Result>
    ): RecyclerView.Adapter<AnimeAdapter.CustomViewHolder>(){

    inner class  CustomViewHolder(view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view =LayoutInflater.from(parent.context).inflate(R.layout.anime_item_layout,parent,false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val anime =animes[position]
            val view = holder.itemView
            val name = view.findViewById<TextView>(R.id.animeName)
            val image= view.findViewById<ImageView>(R.id.imageView)

            name.text = anime.title
            Picasso.get().load(anime.imageUrl).into(image)

             view.setOnClickListener{
                 AnimeDetailsBottomSheet(anime).apply {
                     show(parentActivity.supportFragmentManager,"AnimeDetailsBottomSheet")

                 }
             }
        }

        override fun getItemCount(): Int {
            return animes.size
        }

    }
}