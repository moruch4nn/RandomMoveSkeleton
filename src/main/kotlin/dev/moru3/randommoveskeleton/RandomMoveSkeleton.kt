package dev.moru3.randommoveskeleton

import dev.moru3.minepie.Executor.Companion.runTaskLater
import dev.moru3.minepie.Executor.Companion.runTaskTimer
import dev.moru3.minepie.config.Config
import dev.moru3.minepie.events.EventRegister.Companion.registerEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Skeleton
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class RandomMoveSkeleton: JavaPlugin() {
    var skeleton: Skeleton? = null
    var location: Location? = null
    lateinit var pos1: Location
    lateinit var pos2: Location
    var spawnDelay: Long = 0
    var speed: Double = 1.0
    val config: Config by lazy { Config(this,"config.yml") }

    var targetLocation: Location? = null

    fun spawnSkeleton() {
        this.skeleton?.remove() // スケルトン削除
        val skeleton = pos1.world?.spawn(pos1,Skeleton::class.java)?:return // スケルトンスポーン
        skeleton.setAI(false) // AIを無効化
        skeleton.health = 1.0 // 体力を1.0に設定
        skeleton.isSilent = true // サイレントをtrueに
        skeleton.removeWhenFarAway = false // 離れても消えないように
        this.skeleton = skeleton // スケルトンを設定
    }

    override fun onEnable() {
        config.saveDefaultConfig()
        // コンフィグの変数をロード
        val config = config.config()!!
        val world = Bukkit.getWorld(config.getString("pos1.world")?:"world")?:Bukkit.getWorlds()[0]
        val pos1X = config.getDouble("pos1.x")
        val pos1Y = config.getDouble("pos1.y")
        val pos1Z = config.getDouble("pos1.z")
        val pos2X = config.getDouble("pos2.x")
        val pos2Y = config.getDouble("pos2.y")
        val pos2Z = config.getDouble("pos2.z")
        this.speed = config.getDouble("speed")
        this.spawnDelay = config.getLong("spawnDelay")

        // Locationを追加
        this.pos1 = Location(world,minOf(pos1X,pos2X),minOf(pos1Y,pos2Y),minOf(pos1Z,pos2Z))
        this.pos2 = Location(world,maxOf(pos1X,pos2X),maxOf(pos1Y,pos2Y),maxOf(pos1Z,pos2Z))

        // スケルトンをスポーンさせる
        this.spawnSkeleton()


        // スケルトンが死んだとき、5秒後にスケルトンをスポーンさせる
        this.registerEvent<EntityDeathEvent> { event ->
            // 死んだentityのIDが同じだった場合
            if(event.entity.entityId==this.skeleton?.entityId) {
                // 5秒後にスケルトンをスポーン
                this.runTaskLater(100) { spawnSkeleton() }
            }
        }

        // 1tickおきに実行
        this.runTaskTimer(1,1) {
            val skeleton = this.skeleton?:return@runTaskTimer
            var targetLocation = targetLocation
            val location = this.location?.clone()?:pos1
            if(targetLocation==null) {
                val location = skeleton.location.clone()
                targetLocation = Location(Bukkit.getWorld("world"),(pos1.blockX..pos2.blockX).random().toDouble(),(pos1.blockY..pos2.blockY).random().toDouble(),(pos1.blockZ..pos2.blockZ).random().toDouble())
                location.direction = targetLocation.toVector().subtract(skeleton.location.toVector())
                skeleton.teleport(location)
            }
            this.targetLocation = targetLocation
            if(location.distance(targetLocation) > speed*0.5) {
                location.subtract(cos(Math.toRadians(location.yaw.toDouble() - 90))*speed,tan(Math.toRadians(location.pitch.toDouble()))*speed,sin(Math.toRadians(location.yaw.toDouble() - 90))*speed)
                skeleton.teleport(location)
                this.location = location
            } else {
                this.targetLocation = null
            }
        }
    }
}