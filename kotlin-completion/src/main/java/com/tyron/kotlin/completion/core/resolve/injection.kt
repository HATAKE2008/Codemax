package com.tyron.kotlin.completion.core.resolve

import com.tyron.builder.project.api.KotlinModule
import com.tyron.kotlin.completion.core.resolve.lang.java.resolver.CodemaxTraceBasedJavaResolverCache
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.context.ModuleContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.frontend.java.di.createContainerForLazyResolveWithJava
import org.jetbrains.kotlin.incremental.components.EnumWhenTracker
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.incremental.components.InlineConstTracker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.load.java.JavaClassFinderImpl
import org.jetbrains.kotlin.load.java.components.JavaSourceElementFactoryImpl
import org.jetbrains.kotlin.load.java.lazy.ModuleClassResolver
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.impl.VirtualFileBoundJavaClass
import org.jetbrains.kotlin.load.kotlin.PackagePartProvider
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.CompilerEnvironment
import org.jetbrains.kotlin.resolve.TargetEnvironment
import org.jetbrains.kotlin.resolve.jvm.JavaDescriptorResolver
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProviderFactory

class CodemaxModuleClassResolver(private val sourceScope: GlobalSearchScope) : ModuleClassResolver {
    lateinit var compiledCodeResolver: JavaDescriptorResolver
    lateinit var sourceCodeResolver: JavaDescriptorResolver

    override fun resolveClass(javaClass: JavaClass): ClassDescriptor? {
        val resolver =
            if (javaClass is VirtualFileBoundJavaClass && javaClass.isFromSourceCodeInScope(sourceScope)) {
                sourceCodeResolver
            } else {
                compiledCodeResolver
            }
        return resolver.resolveClass(javaClass)
    }
}

fun createContainerForTopDownAnalyzerForJvm(
    moduleContext: ModuleContext,
    bindingTrace: BindingTrace,
    declarationProviderFactory: DeclarationProviderFactory,
    moduleContentScope: GlobalSearchScope,
    lookupTracker: LookupTracker,
    packagePartProvider: PackagePartProvider,
    jvmTarget: JvmTarget,
    languageVersionSettings: LanguageVersionSettings,
    moduleClassResolver: ModuleClassResolver,
    javaProject: KotlinModule?,
): ComponentProvider = createContainerForLazyResolveWithJava(
    JvmPlatforms.jvmPlatformByTargetVersion(jvmTarget),
    moduleContext,
    bindingTrace,
    declarationProviderFactory,
    moduleContentScope,
    moduleClassResolver,
    CompilerEnvironment,
    lookupTracker,
    ExpectActualTracker.DoNothing,
    InlineConstTracker.DoNothing,
    EnumWhenTracker.DoNothing,
    packagePartProvider,
    languageVersionSettings,
    useBuiltInsProvider = true,
    configureJavaClassFinder = {
        useImpl<JavaClassFinderImpl>()
        useImpl<CodemaxTraceBasedJavaResolverCache>()
        useImpl<JavaSourceElementFactoryImpl>()
    }
)

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> ComponentProvider.get(): T {
    return resolve(T::class.java)?.getValue() as T
}
