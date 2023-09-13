package com.example.githubuser.response

import com.google.gson.annotations.SerializedName

data class FollowerResponse(

	@field:SerializedName("FollowerResponse")
	val followerResponse: List<FollowerResponseItem>
) {
	data class FollowerResponseItem(

		@field:SerializedName("login")
		val login: String,

		@field:SerializedName("avatar_url")
		val avatarUrl: String,

		@field:SerializedName("id")
		val id: Int,
	)
}
