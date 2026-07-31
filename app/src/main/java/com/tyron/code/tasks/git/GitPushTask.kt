package com.tyron.code.tasks.git

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import codemax.rm.R
import codemax.rm.databinding.LayoutDialogProgressBinding
import com.blankj.utilcode.util.ThreadUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tyron.builder.project.Project
import com.tyron.code.ApplicationLoader
import com.tyron.code.tasks.git.GitProgressMonitor
import com.tyron.code.util.executeAsyncProvideError
import org.eclipse.jgit.api.Git

object GitPushTask {

  fun push(project: Project, context: Context) {
    val inflater =
      LayoutInflater.from(context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.layout_dialog_progress, null)
    val binding = LayoutDialogProgressBinding.inflate(inflater, null, false)
    val view = binding.root
    binding.message.visibility = View.VISIBLE
    val builder = MaterialAlertDialogBuilder(context)
    builder.setTitle(R.string.pushing)
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

          val repository = git.repository
          val fullBranch = repository.getFullBranch()
          val branchName = repository.branch
          val refSpec =
            if (branchName != null && branchName.isNotEmpty()) {
              "$branchName:$branchName"
            } else if (fullBranch != null && fullBranch.startsWith("refs/heads/")) {
              val shortName = fullBranch.removePrefix("refs/heads/")
              "$shortName:$shortName"
            } else {
              "HEAD:refs/heads/master"
            }

          val pushCommand = git.push()
          pushCommand.setRemote(remoteName ?: remoteUrl)
          pushCommand.add(refSpec)
          pushCommand.setPushTags()
          pushCommand.setProgressMonitor(progress)

          if (isSsh) {
            pushCommand.setTransportConfigCallback(GitCredentials.sshTransportConfigCallback)
          } else {
            val credentials = GitCredentials.getCredentialsProvider(preferences)
            if (credentials == null) {
              throw IllegalStateException(context.getString(R.string.no_token_configured))
            }
            pushCommand.setCredentialsProvider(credentials)
          }

          pushCommand.call()

          val configuredBranch = branchName?.takeIf { it.isNotEmpty() }
              ?: fullBranch?.takeIf { it.startsWith("refs/heads/") }?.removePrefix("refs/heads/")
          if (configuredBranch != null && remoteName != null) {
            val config = repository.config
            config.setString("branch", configuredBranch, "remote", remoteName)
            config.setString("branch", configuredBranch, "merge", "refs/heads/$configuredBranch")
            config.save()
          }

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
          Toast.makeText(context, context.getString(R.string.push_completed), Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}
