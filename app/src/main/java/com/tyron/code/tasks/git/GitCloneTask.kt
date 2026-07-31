package com.tyron.code.tasks.git

import android.os.Build
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import codemax.rm.R
import codemax.rm.databinding.BaseTextinputLayoutBinding
import codemax.rm.databinding.LayoutDialogProgressBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.os.Environment
import java.io.File
import org.eclipse.jgit.api.Git
import com.tyron.code.util.executeAsyncProvideError
import com.blankj.utilcode.util.ThreadUtils
import com.tyron.builder.project.Project
import android.widget.Toast
import com.tyron.code.ApplicationLoader
import com.tyron.code.tasks.git.GitProgressMonitor

object GitCloneTask {

       fun clone(context:Context) {
       val inflater = LayoutInflater.from(context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater        
       inflater.inflate(R.layout.base_textinput_layout, null)
       
       val binding = BaseTextinputLayoutBinding.inflate(inflater,null,false)
       binding.textinputLayout.setHint(R.string.git_clone_repo_url)
       val builder = MaterialAlertDialogBuilder(context)
       builder.setTitle(R.string.git_clone_repo)
       builder.setView(binding.root)
       builder.setCancelable(true)
       builder.setPositiveButton(R.string.git_clone) { dialog, _ ->
       dialog.dismiss()
       val url = binding.textinputLayout.editText?.text?.toString()
       cloneRepo(url, context)
       }
       builder.setNegativeButton(android.R.string.cancel, null)
   
       builder.show()
       }            
       private fun cloneRepo(repo: String?, context:Context) {
       if (repo.isNullOrBlank()) {
       return
       }

       var url = repo.trim()
       if (!url.endsWith(".git")) {
       url += ".git"
       }
       
       val inflater = LayoutInflater.from(context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater        
       inflater.inflate(R.layout.layout_dialog_progress, null)
       
       val binding = LayoutDialogProgressBinding.inflate(inflater,null,false)
       val view = binding.root
       
       binding.message.visibility = View.VISIBLE
      
       val builder = MaterialAlertDialogBuilder(context)
       builder.setTitle(R.string.git_clone_in_progress)
       builder.setMessage(url)
       builder.setView(view)
       builder.setCancelable(false)
       
       val repoName = url.substringAfterLast('/').substringBeforeLast(".git")
           
       val targetDir:File
       
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
       targetDir = File(ApplicationLoader.applicationContext.getExternalFilesDir("/Projects"), repoName)
       } else {  
       targetDir = File(Environment.getExternalStorageDirectory().absolutePath + "/CodemaxProjects"  , repoName)
       }
       
        val progress = GitProgressMonitor(binding.progress, binding.message)
        var git: Git? = null
        val isSsh = GitCredentials.isSshUrl(url)
        val preferences = ApplicationLoader.getDefaultPreferences()

        val future =
        executeAsyncProvideError(
        {
        return@executeAsyncProvideError Git.cloneRepository()
        .setURI(url)
        .setDirectory(targetDir)
        .apply {
        if (isSsh) {
        setTransportConfigCallback(GitCredentials.sshTransportConfigCallback)
        } else {
        val credentials = GitCredentials.getCredentialsProvider(preferences)
        if (credentials != null) {
        setCredentialsProvider(credentials)
        }
        }
        }
        .setProgressMonitor(progress)
        .call()
        .also { git = it }
        },
        { _, _ -> }
        )
       
       builder.setPositiveButton(android.R.string.cancel) { iface, _ ->
       iface.dismiss()
       progress.cancel()
       Toast.makeText(context, R.string.git_clone_cancled, Toast.LENGTH_SHORT).show()
       git?.close()
       future.cancel(true)
       }
       
      val dialog = builder.show() 
       
       future.whenComplete { result, error ->
       ThreadUtils.runOnUiThread {
       dialog?.dismiss()
     
       if (result == null || error != null) {
       if (!future.isCancelled) {
       showCloneError(error, context)
       }
       } else {
       
       val builder = MaterialAlertDialogBuilder(context)
       builder.setTitle(R.string.success)
       builder.setMessage(url+" " + context.getString(R.string.cloned_successfully))
       builder.setPositiveButton(android.R.string.ok, null)
       builder.show()
       }
       }
       }
       }
       
       private fun showCloneError(error: Throwable, context:Context) {
       val builder = MaterialAlertDialogBuilder(context)
       builder.setTitle(R.string.git_clone_failed)
       builder.setMessage(error.localizedMessage)
       builder.setPositiveButton(android.R.string.ok, null)
       builder.show()
       }
       }