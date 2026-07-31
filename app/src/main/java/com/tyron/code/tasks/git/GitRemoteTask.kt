package com.tyron.code.tasks.git

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import codemax.rm.R
import codemax.rm.databinding.BaseTextinputLayoutBinding
import com.blankj.utilcode.util.ThreadUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tyron.builder.project.Project
import com.tyron.code.util.executeAsyncProvideError
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.StoredConfig

object GitRemoteTask {

  fun remote(project: Project, context: Context) {

    val inflater = LayoutInflater.from(context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.base_textinput_layout, null)

    val binding = BaseTextinputLayoutBinding.inflate(inflater, null, false)
    binding.textinputLayout.setHint(R.string.enter_remote_url)
    val builder = MaterialAlertDialogBuilder(context)
    builder.setTitle(R.string.title_add_remove_remote)
    builder.setView(binding.root)
    builder.setPositiveButton(R.string.add) { _, _ ->
      val future =
        executeAsyncProvideError(
          {
            val remoteUrl = binding.textinputLayout.editText?.text?.toString()?.trim()

            if (remoteUrl.isNullOrBlank()) {
              ThreadUtils.runOnUiThread {
                Toast.makeText(context, context.getString(R.string.empty_remote), Toast.LENGTH_SHORT).show()
              }
            } else {
              val config: StoredConfig = Git.open(project.getRootFile()).repository.config
              config.setString("remote", "origin", "url", remoteUrl)
              config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*")
              config.save()
            }

            return@executeAsyncProvideError
          },
          { _, _ -> }
        )

      future.whenComplete { result, error ->
        ThreadUtils.runOnUiThread {
          if (result == null || error != null) {
            ErrorOutput.ShowError(error, context)
          } else {
            Toast.makeText(context, context.getString(R.string.remote_added_successfully), Toast.LENGTH_SHORT).show()
          }
        }
      }
    }

    builder.setNegativeButton(android.R.string.cancel, null)
    builder.setNeutralButton(R.string.remove) { _, _ ->
      val future =
        executeAsyncProvideError(
          {
            val remoteUrl = binding.textinputLayout.editText?.text?.toString()?.trim()

            if (remoteUrl.isNullOrBlank()) {
              ThreadUtils.runOnUiThread {
                Toast.makeText(context, context.getString(R.string.empty_remote), Toast.LENGTH_SHORT).show()
              }
            } else {
              val remoteName = if (remoteUrl.startsWith("git@") || remoteUrl.startsWith("http")) "origin" else remoteUrl
              val config: StoredConfig = Git.open(project.getRootFile()).repository.config
              config.unsetSection("remote", remoteName)
              config.save()
            }

            return@executeAsyncProvideError
          },
          { _, _ -> }
        )

      future.whenComplete { result, error ->
        ThreadUtils.runOnUiThread {
          if (result == null || error != null) {
            ErrorOutput.ShowError(error, context)
          } else {
            Toast.makeText(context, context.getString(R.string.remote_removed_successfully), Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
    builder.show()
  }
}
