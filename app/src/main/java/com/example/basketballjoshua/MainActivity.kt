package com.example.basketballjoshua

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.title = "NBA Simulation"
        val lineList = ArrayList<String>()
        resources.openRawResource(R.raw.stats).bufferedReader().use {
            for (i in (0..29)) {
                lineList.add(it.readLine())
            }
        }
        val teams = ArrayList<Team>()
        for (i in lineList) {
            val team = Team(i.split(",")[0],
                    i.split(",")[1].toDouble(),
                    i.split(",")[2].toDouble(),
                    i.split(",")[3].toDouble(),
                    i.split(",")[4].toDouble(),
                    i.split(",")[5].toDouble(),
                    i.split(",")[6].toDouble(),
                    i.split(",")[7].toDouble(),
                    i.split(",")[8].toDouble(),
                    i.split(",")[9].toDouble(),
                    i.split(",")[10].toDouble(),
                    i.split(",")[11].toDouble(),
                    i.split(",")[12].toDouble(),
                    i.split(",")[13].toDouble(),
                    i.split(",")[14].toDouble(),
                    i.split(",")[15].toDouble(),
                    i.split(",")[16].toDouble(),
                    i.split(",")[17].toDouble())
            teams.add(team)
        }
        var team1pos = -1
        var team2pos = -1
        val logoTeam1 = findViewById<ImageView>(R.id.logoTeam1)
        val logoTeam2 = findViewById<ImageView>(R.id.logoTeam2)
        val images = arrayOf(R.drawable.hawks, R.drawable.celtics, R.drawable.nets, R.drawable.hornets, R.drawable.bulls, R.drawable.cavaliers, R.drawable.mavericks, R.drawable.nuggets, R.drawable.pistons, R.drawable.warriors, R.drawable.rockets, R.drawable.pacers, R.drawable.clippers, R.drawable.lakers, R.drawable.grizzlies, R.drawable.heat, R.drawable.bucks, R.drawable.timberwolves, R.drawable.pelicans, R.drawable.knicks, R.drawable.thunder, R.drawable.magic, R.drawable.sixers, R.drawable.suns, R.drawable.blazers, R.drawable.kings, R.drawable.spurs, R.drawable.raptors, R.drawable.jazz, R.drawable.wizards)
        val elements = arrayOf("Hawks", "Celtics", "Nets", "Hornets", "Bulls", "Cavaliers", "Mavericks", "Nuggets", "Pistons", "Warriors", "Rockets", "Pacers", "Clippers", "Lakers", "Grizzlies", "Heat", "Bucks", "Timberwolves", "Pelicans", "Knicks", "Thunder", "Magic", "76ers", "Suns", "Trail Blazers", "Kings", "Spurs", "Raptors", "Jazz", "Wizards")
        val selectTeam1 = findViewById<ListView>(R.id.selectTeam1)
        val selectTeam2 = findViewById<ListView>(R.id.selectTeam2)
        val simulate = findViewById<Button>(R.id.simulate)
        val series = findViewById<Button>(R.id.series)
        val result = findViewById<TextView>(R.id.result)
        logoTeam1.setImageResource(R.drawable.nba)
        logoTeam2.setImageResource(R.drawable.nba)

        var listAdapter1: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, elements)
        selectTeam1.adapter = MyAdapter(elements, this)
        selectTeam1.setOnItemClickListener { _, _, position, _ ->
            logoTeam1.setImageResource(images[position])
            team1pos = position
        }

        var listAdapter2: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, elements)
        selectTeam2.adapter = MyAdapter(elements, this)
        selectTeam2.setOnItemClickListener { _, _, position, _ ->
            logoTeam2.setImageResource(images[position])
            team2pos = position
        }

        simulate.setOnClickListener {
            if((team1pos != -1) and (team2pos != -1)) {

                try {
                    var score = sim(teams[team1pos], teams[team2pos])
                    result.text = "" + score[0] + "   -   " + score[1]
                } catch(e: Exception) {
                    result.text = e.toString()
                }
            }
        }

        series.setOnClickListener {

            if((team1pos != -1) and (team2pos != -1)) {
                var gameCounter = 0
                var homeWins = 0
                var awayWins = 0

                while ((homeWins < 4) and (awayWins < 4)) {

                    var score = sim(teams[team1pos], teams[team2pos])
                    if (score[0] > score[1]) {
                        homeWins++
                    }
                    if (score[0] < score[1]) {
                        awayWins++
                    }
                }

                result.text = "" + homeWins + "   -   " + awayWins
            }
        }
    }

    fun sim(home: Team, away: Team): Array<Int> {

        val random = Random()
        val possessions = (home.pace + away.pace).toInt()
        var homeScore = 0
        var awayScore = 0
        var counter = 0

        var turnover: Double
        var shotLocation: Double
        var shotSuccess: Double
        var foul: Double
        var foulSuccess: Double
        var rebound: Double
        var block: Double

        val homeOffensiveReboundSuccess: Double = (1 - ((1 - home.offensiveReboundPercentage) + away.defensiveReboundPercentage) / 2.0).toDouble()
        val awayOffensiveReboundSuccess: Double = (1 - ((1 - away.offensiveReboundPercentage) + home.defensiveReboundPercentage) / 2.0).toDouble()

        var control: Boolean = random.nextBoolean()

        while (counter < possessions) {

            //Home team possession
            if (control) {
                turnover = random.nextDouble()
                if (turnover < home.turnoverPercentage) {
                    control = !control
                } else {
                    shotLocation = random.nextDouble()
                    shotSuccess = random.nextDouble()
                    foul = random.nextDouble()
                    foulSuccess = random.nextDouble()
                    block = random.nextDouble()

                    //Rim Shot
                    if (shotLocation <= home.rimRate) {
                        if ((shotSuccess < home.rimPercentage) and (block > away.blockPercentage)) {
                            homeScore += 2
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 1
                                }
                            }
                        } else {
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 2
                                }
                            }
                        }

                        //Close Shot
                    } else if ((shotLocation > home.closeRate) and (shotLocation <= home.rimRate + home.closeRate)) {
                        if ((shotSuccess < home.closePercentage) and (block > away.blockPercentage)) {
                            homeScore += 2
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 1
                                }
                            }
                        } else {
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 2
                                }
                            }
                        }

                        //Mid Shot
                    } else if ((shotLocation > home.rimRate + home.closeRate) and (shotLocation <= home.rimRate + home.closeRate + home.mediumRate)) {
                        if ((shotSuccess < home.mediumPercentage) and (block > away.blockPercentage)) {
                            homeScore += 2
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 1
                                }
                            }
                        } else {
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 2
                                }
                            }
                        }

                        //Long Shot
                    } else if ((shotLocation > home.rimRate + home.closeRate + home.mediumRate) and (shotLocation <= home.rimRate + home.closeRate + home.mediumRate + home.longRate)) {
                        if ((shotSuccess < home.longPercentage) and (block > away.blockPercentage)) {
                            homeScore += 2
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 1
                                }
                            }
                        } else {
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 2
                                }
                            }
                        }

                        //Three Shot
                    } else {
                        if ((shotSuccess < home.threePercentage) and (block > away.blockPercentage)) {
                            homeScore += 3
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 1
                                }
                            }
                        } else {
                            if (foul < home.freeRate) {
                                if (foulSuccess < home.freePercentage) {
                                    homeScore += 3
                                }
                            }
                        }
                    }
                    rebound = random.nextDouble()
                    if (rebound > homeOffensiveReboundSuccess) {
                        control = !control
                    }
                }
                counter++
            }

            //Away team possession
            if (!control) {
                turnover = random.nextDouble()
                if (turnover < away.turnoverPercentage) {
                    control = !control
                } else {
                    shotLocation = random.nextDouble()
                    shotSuccess = random.nextDouble()
                    foul = random.nextDouble()
                    foulSuccess = random.nextDouble()
                    block = random.nextDouble()

                    //Rim Shot
                    if (shotLocation <= away.rimRate) {
                        if ((shotSuccess < away.rimPercentage) and (block > home.blockPercentage)) {
                            awayScore += 2
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 1
                                }
                            }
                        } else {
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 2
                                }
                            }
                        }

                        //Close Shot
                    } else if ((shotLocation > away.closeRate) and (shotLocation <= away.rimRate + away.closeRate)) {
                        if ((shotSuccess < away.closePercentage) and (block > home.blockPercentage)) {
                            awayScore += 2
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 1
                                }
                            }
                        } else {
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 2
                                }
                            }
                        }

                        //Mid Shot
                    } else if ((shotLocation > away.rimRate + away.closeRate) and (shotLocation <= away.rimRate + away.closeRate + away.mediumRate)) {
                        if ((shotSuccess < away.mediumPercentage) and (block > home.blockPercentage)) {
                            awayScore += 2
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 1
                                }
                            }
                        } else {
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 2
                                }
                            }
                        }

                        //Long Shot
                    } else if ((shotLocation > away.rimRate + away.closeRate + away.mediumRate) and (shotLocation <= away.rimRate + away.closeRate + away.mediumRate + away.longRate)) {
                        if ((shotSuccess < away.longPercentage) and (block > home.blockPercentage)) {
                            awayScore += 2
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 1
                                }
                            }
                        } else {
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 2
                                }
                            }
                        }

                        //Three Shot
                    } else {
                        if ((shotSuccess < away.threePercentage) and (block > home.blockPercentage)) {
                            awayScore += 3
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 1
                                }
                            }
                        } else {
                            if (foul < away.freeRate) {
                                if (foulSuccess < away.freePercentage) {
                                    awayScore += 3
                                }
                            }
                        }
                    }
                    rebound = random.nextDouble()
                    if (rebound > awayOffensiveReboundSuccess) {
                        control = !control
                    }
                }
                counter++
            }
        }

        return arrayOf(homeScore, awayScore)
    }
}

class Team(var name: String,
           var pace: Double,
           var turnoverPercentage: Double,
           var defensiveReboundPercentage: Double,
           var offensiveReboundPercentage: Double,
           var rimRate: Double,
           var closeRate: Double,
           var mediumRate: Double,
           var longRate: Double,
           var threeRate: Double,
           var freeRate: Double,
           var rimPercentage: Double,
           var closePercentage: Double,
           var mediumPercentage: Double,
           var longPercentage: Double,
           var threePercentage: Double,
           var freePercentage: Double,
           var blockPercentage: Double)

class MyAdapter(private var data: Array<String>, private var context: Context): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tv = TextView(context)
        tv.text = data[position]
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28F)
        tv.setPadding(10, 10, 10, 10)
        tv.gravity = Gravity.CENTER;
        return tv
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}