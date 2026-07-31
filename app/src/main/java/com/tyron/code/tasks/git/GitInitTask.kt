package com.tyron.code.tasks.git

import android.content.Context
import android.widget.Toast
import codemax.rm.R
import com.blankj.utilcode.util.ThreadUtils
import com.tyron.builder.project.Project
import com.tyron.code.ApplicationLoader
import com.tyron.code.tasks.git.ErrorOutput
import com.tyron.code.util.executeAsyncProvideError
import com.tyron.common.SharedPreferenceKeys
import java.io.File
import org.eclipse.jgit.api.Git

object GitInitTask {

  fun init(project: Project, context: Context) {

    val file = File(project.getRootFile(), "/.git")
    val path = file.toString()
    val preferences = ApplicationLoader.getDefaultPreferences()
    val userName = preferences.getString(SharedPreferenceKeys.GIT_USER_NAME, "")
    val userEmail = preferences.getString(SharedPreferenceKeys.GIT_USER_EMAIL, "")
    val future =
      executeAsyncProvideError({
        val git = Git.init().setDirectory(project.getRootFile()).call()
        val config = git.repository.config
        if (!userName.isNullOrBlank()) {
          config.setString("user", null, "name", userName)
        }
        if (!userEmail.isNullOrBlank()) {
          config.setString("user", null, "email", userEmail)
        }
        config.save()

        if (file.exists()) {
          ThreadUtils.runOnUiThread {
            Toast.makeText(context, context.getString(R.string.git_reinitialized, path), Toast.LENGTH_SHORT).show()
          }
        } else {
          ThreadUtils.runOnUiThread {
            Toast.makeText(context, context.getString(R.string.git_initialized, path), Toast.LENGTH_SHORT).show()
          }
        }

        return@executeAsyncProvideError
      }, { _, _ -> })

    future.whenComplete { result, error ->
      ThreadUtils.runOnUiThread {
        if (result == null || error != null) {
          ErrorOutput.ShowError(error, context)
        }
      }
    }
  }
}
