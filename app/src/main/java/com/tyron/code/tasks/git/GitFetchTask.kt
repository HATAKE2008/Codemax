package com.tyron.code.tasks.git

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import codemax.rm.R
import codemax.rm.databinding.LayoutDialogProgressBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.blankj.utilcode.util.ThreadUtils
import com.tyron.builder.project.Project
import com.tyron.code.ApplicationLoader
import com.tyron.code.tasks.git.GitProgressMonitor
import com.tyron.code.util.executeAsyncProvideError
import android.widget.Toast
import org.eclipse.jgit.api.Git

object GitFetchTask {

  fun fetch(project: Project, context: Context) {

    val inflater = LayoutInflater.from(context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.layout_dialog_progress, null)

    val binding = LayoutDialogProgressBinding.inflate(inflater, null, false)
    val view = binding.root

    binding.message.visibility = View.VISIBLE

    val builder = MaterialAlertDialogBuilder(context)
    builder.setTitle(R.string.title_fetching)
    builder.setView(view)
    builder.setCancelable(false)

    val progress = GitProgressMonitor(binding.progress, binding.message)
    val preferences = ApplicationLoader.getDefaultPreferences()

    val future =
      executeAsyncProvideError(
        {
          val git = Git.open(project.getRootFile())
          val (remoteName, remoteUrl) = GitCredentials.resolveRemoteUrl(git, preferences)

          if (remoteUrl.isNullOrBlank()) {
            throw IllegalStateException(context.getString(R.string.no_remote_configured))
          }

          val isSsh = GitCredentials.isSshUrl(remoteUrl)

          val fetchCommand = git.fetch()
          fetchCommand.setRemote(remoteName ?: remoteUrl)
          fetchCommand.setProgressMonitor(progress)

          if (isSsh) {
            fetchCommand.setTransportConfigCallback(GitCredentials.sshTransportConfigCallback)
          } else {
            val credentials = GitCredentials.getCredentialsProvider(preferences)
            if (credentials != null) {
              fetchCommand.setCredentialsProvider(credentials)
            }
          }

          fetchCommand.call()

          return@executeAsyncProvideError
        },
        { _, _ -> }
      )

    val dialog = builder.show()

    future.whenComplete { result, error ->
      ThreadUtils.runOnUiThread {
        dialog?.dismiss()

        if (result == null || error != null) {
          ErrorOutput.ShowError(error, context)
        } else {
          Toast.makeText(context, context.getString(R.string.fetch_completed), Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}
}