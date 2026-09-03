package com.example.simplecamera.data

import com.example.simplecamera.ui.camera.CameraMode
import com.example.simplecamera.ui.camera.ModeTarget
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * -------------------------------------------------------------
 * TILTSHIFT* ROASTS & LINKEDIN CAPTIONS REPOSITORY
 * -------------------------------------------------------------
 * You can freely edit and add your own savage roasts and corporate
 * parody LinkedIn captions in the lists below!
 * -------------------------------------------------------------
 */

object RoastsRepository {

    // ---------------------------------------------------------
    // 1. VICIOUS & SAVAGE ROASTS (Customizable by user)
    // ---------------------------------------------------------

    val EXTREME_PITCH_ROASTS = listOf(
        "Off by %d°! Are you trying to photograph the ceiling or the subterranean mole people?!",
        "Off by %d°! Put the phone down and step away from photography forever.",
        "Off by %d°! Even the leaning tower of Pisa feels upright looking at this.",
        "Off by %d°! Did you drop the phone down a flight of stairs and call it art?",
        "Off by %d°! My gyroscope is crying silicon tears right now.",
        "Off by %d°! A blind pigeon on a rollercoaster has better horizon awareness.",
        "Off by %d°! NASA called, they want their lunar descent angle back.",
        "Off by %d°! Are you taking a photo or testing earthquake fault lines?",
        "Off by %d°! Gravity is literally free and you still managed to defy it horribly.",
        "Off by %d°! Did you sneeze mid-shot or is this your genuine lack of motor skills?"
    )

    val MODERATE_PITCH_ROASTS = listOf(
        "Crooked by %s°! My grandma takes straighter photos while knitting in an earthquake.",
        "Off by %s°! Are you steering a 17th-century pirate ship through a typhoon?",
        "Off by %s°! If crooked was a competitive Olympic sport, you would take gold.",
        "Off by %s°! The horizon called. It wants a restraining order against your hands.",
        "Off by %s°! That tilt is aggressive enough to slide coffee off a flat table.",
        "Off by %s°! You had one job: hold the glass rectangle flat. You failed.",
        "Off by %s°! That angle is giving pure Dutch-angle student film catastrophe.",
        "Off by %s°! Steady those jittery fingers, this isn't a maraca solo.",
        "Off by %s°! Just 10 more degrees and you'll invent a brand new geometric defect.",
        "Off by %s°! Even a broken spirit level would point at you and laugh."
    )

    val SLIGHT_PITCH_ROASTS = listOf(
        "Missed by just %s°! So agonizingly close yet completely incompetent.",
        "Crooked by %s°! Did you breathe? Because that breath ruined everything.",
        "Off by %s°! One millimeter of discipline was all you needed. Tragic.",
        "Off by %s°! Almost had it, but your nervous system surrendered at the last millisecond.",
        "Missed by %s°! That microscopic tilt is louder than a jet engine to my sensors.",
        "Off by %s°! Perfection slipped through your shaky little fingers.",
        "Off by %s°! Close only counts in horseshoes and hand grenades, not TiltShift.",
        "Missed by %s°! You were on the verge of greatness, then you wobbled."
    )

    val ZOOM_ROASTS = listOf(
        "Off by %sx zoom! Dragging a digital slider across a screen was too high IQ for you?",
        "Missed zoom target by %sx! It's a slider, not a Rubik's cube, genius.",
        "Off by %sx zoom! Can't match two identical numbers on a screen? Tragic.",
        "Missed zoom by %sx! Your thumb has the precision of a potato masher.",
        "Zoom error: %sx! Optical sensors are weeping in high definition."
    )

    val COMPASS_ROASTS = listOf(
        "Off by %d° from target! Do you even know which hemisphere you are standing in?",
        "Wrong heading by %d°! Christopher Columbus navigated better with scurvy.",
        "Off by %d°! Even Google Maps gave up trying to reroute your direction.",
        "Facing the wrong way by %d°! The target was that way, captain blind-spot.",
        "Off by %d° on compass! North is where the needle pointed, not your ego."
    )

    val ACCIDENTAL_PERFECTION_ROASTS = listOf(
        "Surprisingly, you didn't butcher this shot. Pure beginners luck.",
        "Miracles happen: you actually aligned it. Don't let it get to your head.",
        "Shot verified accurate. Even a broken clock is right twice a day.",
        "Target matched perfectly. I suspect you placed the phone on a table to cheat.",
        "Flawless alignment achieved. Frame this, because you will never do it again.",
        "100%% conformance detected. Sensor audit stunned by unexpected competence."
    )

    // ---------------------------------------------------------
    // 2. LIVE VIEW "YOU HAVE A MESSAGE" BANNER ROASTS
    // ---------------------------------------------------------

    fun getLiveMessageText(pErr: Float, isLocked: Boolean): String {
        return if (isLocked) {
            "Alignment holding steady! Don't tremble, capture now."
        } else when {
            pErr > 18f -> "Off by ${pErr.roundToInt()}°! Are you trying to photograph the ceiling or floor?!"
            pErr > 12f -> "Off by ${pErr.roundToInt()}°! Steering a pirate ship with that phone?"
            pErr > 7f -> "Off by ${pErr.roundToInt()}°! Hands trembling like a 7.0 earthquake."
            pErr > 3f -> "Crooked by ${String.format(Locale.US, "%.1f", pErr)}°! My grandma tilts straighter than this."
            else -> "Almost aligned (${String.format(Locale.US, "%.1f", pErr)}° left)! Hold your breath and steady up."
        }
    }

    // ---------------------------------------------------------
    // 3. MAIN SAVAGE ROAST GENERATOR
    // ---------------------------------------------------------

    fun generateSavageRoast(
        mode: CameraMode,
        pitchErr: Float,
        compassErr: Float,
        zoomErr: Float
    ): String {
        return when {
            mode != CameraMode.NORMAL && pitchErr > 12f -> {
                String.format(Locale.US, EXTREME_PITCH_ROASTS.random(), pitchErr.roundToInt())
            }
            mode != CameraMode.NORMAL && pitchErr > 4f -> {
                String.format(Locale.US, MODERATE_PITCH_ROASTS.random(), String.format(Locale.US, "%.1f", pitchErr))
            }
            mode != CameraMode.NORMAL && pitchErr > 0.5f -> {
                String.format(Locale.US, SLIGHT_PITCH_ROASTS.random(), String.format(Locale.US, "%.1f", pitchErr))
            }
            zoomErr > 0.08f -> {
                String.format(Locale.US, ZOOM_ROASTS.random(), String.format(Locale.US, "%.2f", zoomErr))
            }
            mode == CameraMode.PEAK && compassErr > 2.0f -> {
                String.format(Locale.US, COMPASS_ROASTS.random(), compassErr.roundToInt())
            }
            else -> ACCIDENTAL_PERFECTION_ROASTS.random()
        }
    }

    // ---------------------------------------------------------
    // 4. 20 HILARIOUS CORPORATE / LINKEDIN PARODY CAPTIONS
    // ---------------------------------------------------------

    val LINKEDIN_PARODY_POSTS = listOf(
        // 1. High Accuracy Chad
        "I spent 12 minutes holding my phone at a precise 37° tilt today.\n\nHere is what intentional sensor friction taught me about B2B enterprise sales:\n\n1. Most people take the easy shot. True leaders align under constraint.\n2. Micro-adjustments matter. Being off by 2 degrees is the difference between closing a $10M ARR contract and getting left on read.\n3. The grind never sleeps.\n\nAttached is my Official Certificate of Photographic Conformity. Agree?\n\n#Leadership #B2BSaaS #Mindset #Discipline #TiltShift",

        // 2. Crooked Failure Reflection
        "Today I failed to hold my smartphone level. I missed the target by 28 degrees.\n\nAnd it was the best executive coaching session of my career.\n\nIn business, we often think our horizon is straight. But without real-time telemetry (OKRs, KPIs, hardware gyroscopes), we drift.\n\nEmbrace your crooked angles. The market will roast you, but resilience will build you.\n\nWhat are you misaligning this quarter?\n\n#GrowthMindset #VulnerabilityInLeadership #FailForward #Disruption",

        // 3. Peak Mode Perfection
        "Proud to announce that I have achieved S-Tier Hardware Conformance Certification in Peak Mode.\n\nWhile others were scrolling reels, I was harmonizing:\n- Focal Zoom (3.5x)\n- Pitch Angle (45°)\n- Compass Azimuth (180° South)\n\nSynergy isn't a buzzword. It's an operational posture.\n\nBig thank you to my mentors, my coffee mug, and the built-in accelerometer for keeping me accountable.\n\n#HighPerformance #Excellence #AuditReady #Synergy #DeepWork",

        // 4. Hustle Culture
        "Wake up at 4:30 AM.\nIce bath.\nCold brew.\nAlign phone camera to exactly 52° tilt while holding plank position.\n\nIf you can't control the tilt of your smartphone, you can't control a 50-person product organization.\n\nStay hungry. Stay tilted.\n\n#Hustle #FoundersJourney #ExecutivePresence #NoExcuses #Discipline",

        // 5. AI & Hardware Satire
        "Is AI coming for your camera? No.\n\nHardware sensors that verbally abuse you when your hands shake are coming for your camera.\n\nToday, an on-device algorithm called my photography 'giving Dutch-angle catastrophe'. It was the most honest performance review I’ve received in 7 years.\n\nHere's my verified audit breakdown below. Thoughts?\n\n#ArtificialIntelligence #PerformanceManagement #AuthenticLeadership #TechTrends",

        // 6. Resilience & Agility
        "Pivot. Iterate. Re-align.\n\nWhen TiltShift demanded a 60° angle with 2.0x zoom, my initial reaction was pushback. Legacy thinking.\n\nInstead, I pivoted my wrists 14 degrees clockwise, absorbed the live roast, and executed.\n\nAgile isn't a framework—it's how you hold your phone.\n\n#Agile #ScrumMaster #ProductManagement #ContinuousImprovement #Pivot",

        // 7. Humblebrag
        "Humbled and honored to share that I have been awarded the Certificate of Conformity by TiltShift*.\n\nNever thought a crooked photo of a desk chair could teach me so much about supply chain optimization.\n\nNever stop learning.\n\n#Blessed #Humbled #Gratitude #ProfessionalDevelopment",

        // 8. Contrarian Take
        "Unpopular opinion: Perfectly straight photos are a scam propagated by middle management.\n\nTrue innovators shoot at 73 degrees with magnetic compass interference.\n\nChallenge the orthodoxy. Break the grid.\n\n#Contrarian #Innovation #ThinkingOutsideTheBox #Disruption #Creativity",

        // 9. Startup Culture
        "We are in stealth mode, but I can officially share this:\n\nOur pre-seed burn rate is low, our runway is long, and our phone camera tilt accuracy is currently tracking at 97.4% MoM growth.\n\nInvestors, DMs are open.\n\n#Startup #VentureCapital #Fundraising #SaaS #PreSeed",

        // 10. Data-Driven Leadership
        "You can't manage what you don't measure.\n\n- Zoom Delta: ±0.02x\n- Pitch Variance: ±1.2°\n- Roast Severity: Level 5 Sarcasm\n\nData-driven discipline transforms chaotic photos into verified operational assets.\n\n#DataAnalytics #MetricsMatter #KPIs #OperationalExcellence",

        // 11. Overcoming Imposter Syndrome
        "I almost didn't post this.\n\nI was afraid my network would judge me for being off by 6 degrees on my camera tilt. Imposter syndrome is real.\n\nThen I remembered: even the greatest CEOs have had unstable gyroscopes at some point in their careers.\n\nBe bold. Share your uncalibrated moments.\n\n#ImposterSyndrome #MentalHealth #Authenticity #WorkplaceCulture",

        // 12. Cross-Functional Collaboration
        "Notice how the Zoom, Tilt, and Compass requirements all had to turn green simultaneously before the shutter unlocked?\n\nThat's not just a puzzle camera.\n\nThat's Product, Engineering, and Sales finally agreeing on a sprint deadline.\n\nTag a colleague who needs to see this cross-functional alignment.\n\n#CrossFunctional #Teamwork #Collaboration #SprintGoals",

        // 13. Customer Centricity
        "Customer: 'I just want to take a photo of my lunch.'\n\nTiltShift*: 'Align to 45° North-West or starve.'\n\nSometimes, radical constraints deliver the highest user delight. Here is what this teaches us about UX friction...\n\n#UserExperience #ProductDesign #CustomerCentric #UXResearch",

        // 14. Burnout & Balance
        "Work-life balance is like the TiltShift spirit level bubble: if you lean too far forward, you get roasted; if you lean too far back, you miss the target.\n\nFind your zero-degree equilibrium today.\n\n#WorkLifeBalance #Wellness #BurnoutPrevention #SelfCare",

        // 15. The Power of Feedback
        "TiltShift roast message: 'My grandma tilts straighter than this.'\n\nMost people would file an HR complaint.\n\nA true growth-oriented professional says: 'Thank you for this constructive feedback. How can I optimize my wrist flexion for Q3?'\n\nFeedback is a gift.\n\n#RadicalCandor #FeedbackIsAGift #Coaching #PersonalGrowth",

        // 16. Remote Work Culture
        "Working remotely means you have to self-govern your horizon.\n\nNo manager was in the room telling me to tilt my phone to 25°. The hardware sensor was my scrum master.\n\nAsync accountability is the future of work.\n\n#RemoteWork #FutureOfWork #DigitalNomad #SelfDiscipline",

        // 17. The 1% Marginal Gains
        "Sir Dave Brailsford spoke about the aggregation of marginal gains.\n\n1% less hand tremor.\n1% more compass precision.\n1% sharper zoom.\n\nToday's 98.2% conformity score wasn't built in a day. It was built in 47 retries and 3 broken promises.\n\n#MarginalGains #AtomicHabits #Kaizen #Consistency",

        // 18. Storytelling in Business
        "Every photograph tells a story. But a photo locked at 81° tilt tells a story of perseverance, hardware telemetry, and defiance against optical mediocrity.\n\nHere is how to weave operational friction into your brand narrative:\n\n#BrandStorytelling #MarketingStrategy #ThoughtLeadership",

        // 19. Extreme Ownership
        "When the sensor said 'Off by 34°', did I blame the lighting? No.\nDid I blame the Asus gyroscope? No.\n\nI took Extreme Ownership of my crooked posture and re-calibrated.\n\nLead from the front.\n\n#ExtremeOwnership #LeadershipLessons #DisciplineEqualsFreedom",

        // 20. The Ultimate Chad
        "They said Chad Mode was impossible without the spirit level.\n\nThey said a human hand cannot sense 45 degrees of tilt by pure intuition.\n\nThey were wrong.\n\nCertified Chad Mode Capture verified below. Stay uncommon.\n\n#ChadMode #Uncommon #Excellence #AchievementUnlocked #TiltShift"
    )

    /**
     * Intelligently select the best LinkedIn post depending on the shot results!
     */
    fun selectBestLinkedInPost(
        accuracy: Float,
        grade: String,
        mode: CameraMode,
        pitchErr: Float
    ): String {
        return when {
            // Chad Mode master
            accuracy >= 97f && mode == CameraMode.PEAK -> LINKEDIN_PARODY_POSTS[2] // Peak Mode Perfection
            accuracy >= 95f -> LINKEDIN_PARODY_POSTS[0] // High Accuracy Chad
            pitchErr > 20f -> LINKEDIN_PARODY_POSTS[1] // Crooked Failure Reflection
            pitchErr > 12f -> LINKEDIN_PARODY_POSTS[10] // Overcoming Imposter Syndrome
            mode == CameraMode.PEAK -> LINKEDIN_PARODY_POSTS[11] // Cross-Functional
            accuracy >= 90f -> LINKEDIN_PARODY_POSTS[16] // Marginal Gains
            pitchErr > 5f -> LINKEDIN_PARODY_POSTS[14] // Power of Feedback
            else -> LINKEDIN_PARODY_POSTS.random()
        }
    }
}
