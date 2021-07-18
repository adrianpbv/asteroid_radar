package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidAdapter
import com.udacity.asteroidradar.ui.Asteroid
import com.udacity.asteroidradar.ui.NasaImage
import java.text.SimpleDateFormat
import java.util.*

/**
 * Bind the recycler view with all the Asteroids data
 */
@BindingAdapter("listAsteroidsData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidAdapter
    adapter.submitList(data) {
        // scroll the list to the top after the diffs are calculated and posted
        recyclerView.scrollToPosition(0)
    }
}

/**
 * When there is not data to show on the [RecyclerView] while it is loading, the [ProgressBar] view
 * is shown, otherwise the data is shown on the [RecyclerView] and the [ProgressBar] view is hidden
 */
@BindingAdapter("goneIfNotNull")
fun View.goneIfNotNull(it: Any?) {
    visibility = if (it != null) View.GONE else View.VISIBLE
}

/**
 * Binding adapter used to display images from an URL using Picasso
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, picture: NasaImage?) {
    picture?.let {
        if (picture.url != "")// check in case the thumbnail is ""
            Picasso.with(imageView.context).load(it.url)
                .placeholder(R.drawable.placeholder_picture_of_day)
                .error(R.drawable.unavailable_image_24)
                .into(imageView)
    }
}

/**
 * Set the text to indicate the state of the Nasa Day Image, if there is none, it indicates the user
 * that there is no an image to show
 */
@BindingAdapter("titleImgDay")
fun setTitleImage(view: TextView, url: String?) {
    url?.let {
        val context = view.context
        if (it == "")
            view.text = context.getString(R.string.unavailable_image)
        else
            view.text = context.getString(R.string.image_of_the_day)
    }
}

//Binding adapters to bind the Asteroid Information with the Details screen

@BindingAdapter("asteroidName")
fun setNameAsteroid(view: TextView, name: String?) {
    name?.let {
        view.text = name
    }
}

@BindingAdapter("asteroidDate")
fun setAsteroidDate(view: TextView, date: String?) {
    date?.let {
        val fromNasaDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date_formatted = SimpleDateFormat(
            view.context.getString(R.string.date_format),
            Locale.getDefault())
        val date_parsed = fromNasaDate.parse(date)!!
        view.text = date_formatted.format(date_parsed)
    }
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("setcontentDescriptionTitle")
fun setContentDescriptionTitle(view: View, title: String?) {
    title?.let {
        view.contentDescription =
            view.context.getString(R.string.description_title_nasapicture, title)
    }
}

/**
 * Set the ContentDescription to each Asteroid on the RecyclerView
 */
@BindingAdapter("setContentDescription")
fun setContentDescriptionAsteroid(view: View, asteroid: Asteroid?) {
    asteroid?.let {
        view.contentDescription = view.context.getString(
            R.string.description_asteroid,
            asteroid.codename, asteroid.distanceFromEarth, asteroid.closeApproachDate
        )
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        // setting up content description according to the asteroid dangerousness
        imageView.contentDescription = context.getString(R.string.description_hazardous_asteroid)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = context.getString(R.string.description_no_hazardous_asteroid)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
