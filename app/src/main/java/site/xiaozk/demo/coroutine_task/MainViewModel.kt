package site.xiaozk.demo.coroutine_task

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException
import kotlin.random.Random

/**
 * @author: xiaozhikang
 * @mail: xiaozhikang0916@gmail.com
 * @create: 2021/12/25
 */
const val TAG = "TaskLog"
class MainViewModel : ViewModel() {
    val mainTask = MutableStateFlow(MainTask(TaskStatus.Init))
    suspend fun startMainTask() {
        mainTask.emit(MainTask(TaskStatus.Loading))
        try {
            coroutineScope {
                awaitAll(
                    async { startSubTask(1) },
                    async { startSubTask(2) },
                    async { startSubTask(3) },
                )
            }
            val lastTask = startSubTask(4)
            Log.i(TAG, "Last task result $lastTask")
            mainTask.emit(MainTask(TaskStatus.Succeed))
        } catch (e: Exception) {
            // 有子任务失败，此处不太关心具体是谁失败了，只需要通知失败状态
            mainTask.emit(MainTask(TaskStatus.Failed, e))
        }
    }

    suspend fun startSubTask(id: Long): Long {
        Log.i(TAG, "Start task of $id")
        try {
            delay((id + 1) * 500)
        } catch (e: CancellationException) {
            // 并发执行的其他任务失败了，此任务不再继续，直接取消
            Log.i(TAG, "Task $id cancelled by others", e)
            throw e
        }
        val rand = Random.nextInt(3)
        Log.i(TAG, "Task $id random $rand")
        if (rand > 0) {
            Log.i(TAG, "Task $id success, returning")
            return id * rand
        }
        Log.i(TAG, "Task $id failed, throwing")
        throw TaskException(id)
    }
}

data class MainTask(
    val status: TaskStatus,
    val cause: Throwable? = null
)

enum class TaskStatus {
    Init,
    Loading,
    Succeed,
    Failed
}

class TaskException(id: Long) : IOException("$id is failed")