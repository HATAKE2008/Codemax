package com.tyron.code.tasks.git

import android.content.SharedPreferences
import com.tyron.code.ui.ssh.callback.SshTransportConfigCallback
import com.tyron.common.SharedPreferenceKeys
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

object GitCredentials {

  fun isSshUrl(url: String?): Boolean {
    if (url.isNullOrBlank()) return false
    val trimmed = url.trim()
    return trimmed.startsWith("git@") || trimmed.startsWith("ssh://")
  }

  fun getRemoteUrl(preferences: SharedPreferences): String? {
    return preferences.getString(SharedPreferenceKeys.GIT_REMOTE_URL, "")?.trim()?.takeIf { it.isNotEmpty() }
  }

  fun getAccessToken(preferences: SharedPreferences): String? {
    return preferences.getString(SharedPreferenceKeys.GIT_ACCESS_TOKEN, "")?.trim()?.takeIf { it.isNotEmpty() }
  }

  fun getUserName(preferences: SharedPreferences): String? {
    return preferences.getString(SharedPreferenceKeys.GIT_USER_NAME, "")?.trim()?.takeIf { it.isNotEmpty() }
  }

  fun getCredentialsProvider(preferences: SharedPreferences): CredentialsProvider? {
    val token = getAccessToken(preferences)
    if (token.isNullOrEmpty()) {
      return null
    }
    val user = getUserName(preferences)
    val username = if (user.isNullOrEmpty()) token else user
    return UsernamePasswordCredentialsProvider(username, token)
  }

  fun resolveRemoteUrl(git: Git, preferences: SharedPreferences): Pair<String?, String?> {
    val config = git.repository.config
    val subsections = config.getSubsections("remote")
    for (name in subsections) {
      val url = config.getString("remote", name, "url")
      if (!url.isNullOrBlank()) {
        return Pair(name, url)
      }
    }
    return Pair(null, getRemoteUrl(preferences))
  }

  val sshTransportConfigCallback = SshTransportConfigCallback()
}
