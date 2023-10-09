package com.example.fifthsemproject.domain.models.codeforces

import com.google.firebase.firestore.auth.User

data class CodeforcesUserInfoResponse(
    val status: String= "",
    val result: List<CodeforcesUser> = listOf()
)

data class CodeforcesUser(
    val lastName: String,
    val country: String,
    val lastOnlineTimeSeconds: Long,
    val city: String,
    val rating: Int,
    val friendOfCount: Int,
    val titlePhoto: String,
    val handle: String,
    val avatar: String,
    val firstName: String,
    val contribution: Int,
    val organization: String,
    val rank: String,
    val maxRating: Int,
    val registrationTimeSeconds: Long,
    val maxRank: String
)

data class SubmissionsResponse(
    val status: String = "",
    val result: List<Submission> = listOf()
)

data class Problem(
    val contestId: Int,
    val index: String,
    val name: String,
    val type: String,
    val rating: Int,
    val tags: List<String>
)

data class Author(
    val contestId: Int,
    val members: List<Member>,
    val participantType: String,
    val ghost: Boolean,
    val startTimeSeconds: Long
)

data class Member(
    val handle: String
)

data class Submission(
    val id: Int,
    val contestId: Int,
    val creationTimeSeconds: Long,
    val relativeTimeSeconds: Long,
    val problem: Problem,
    val author: Author,
    val programmingLanguage: String,
    val verdict: String,
    val testset: String,
    val passedTestCount: Int,
    val timeConsumedMillis: Int,
    val memoryConsumedBytes: Int
)

data class Contest(
    val contestId: Int,
    val contestName: String,
    val handle: String,
    val rank: Int,
    val ratingUpdateTimeSeconds: Long,
    val oldRating: Int,
    val newRating: Int
)

data class RatingResponse(
    val status: String = "",
    var result: List<Contest> = listOf()
)