package org.spekframework.spek2.runtime

import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.runtime.execution.DiscoveryRequest
import org.spekframework.spek2.runtime.execution.DiscoveryResult
import org.spekframework.spek2.runtime.execution.ExecutionRequest
import org.spekframework.spek2.runtime.lifecycle.LifecycleManager
import org.spekframework.spek2.runtime.scope.GroupScopeImpl
import org.spekframework.spek2.runtime.scope.Path
import org.spekframework.spek2.runtime.scope.ScopeId
import org.spekframework.spek2.runtime.scope.ScopeType

abstract class AbstractRuntime {
    abstract fun discover(discoveryRequest: DiscoveryRequest): DiscoveryResult

    protected fun resolveSpec(instance: Spek, path: Path): GroupScopeImpl {
        val fixtures = FixturesAdapter()
        val lifecycleManager = LifecycleManager().apply {
            addListener(fixtures)
        }

        val qualifiedName = (path.parent?.name ?: "") + ".${path.name}"
        val classScope =
            GroupScopeImpl(ScopeId(ScopeType.Class, qualifiedName), path, null, Skip.No, lifecycleManager, false)
        instance.root.invoke(Collector(classScope, lifecycleManager, fixtures, CachingMode.TEST))

        return classScope
    }

    fun execute(request: ExecutionRequest) = Executor().execute(request)
}

expect class SpekRuntime() : AbstractRuntime
