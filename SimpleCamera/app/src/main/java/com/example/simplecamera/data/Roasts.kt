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

    val EXPRESSION_ROASTS = listOf(
        "Off-target face! You looked like a startled alpaca trying to match that %s.",
        "Your facial muscles filed for bankruptcy halfway through that %s attempt.",
        "That expression was 90%% panic and 10%% confusion. The camera lens is traumatized.",
        "You were asked for %s, but you delivered pure existential dread.",
        "AI face detection almost called emergency services trying to classify your face.",
        "Close enough to %s, if %s meant dropping your jaw in complete helplessness.",
        "That face gave pure 'student who forgot there was a final exam today' energy."
    )

    val PEAK_PLUS_ARM_EYES_ROASTS = listOf(
        "Arm trembling at %d cm! Are your biceps made of cooked spaghetti?",
        "Held at %d cm with eyes shut: You look like you're meditating on an airplane emergency exit.",
        "Full 70cm stretch achieved while completely blind to your own surroundings. Inspiring.",
        "Taking photos 70cm away with eyes closed: peak trust, or pure sensory deprivation.",
        "Your triceps endured the %d cm torture test while your eyelids did all the acting.",
        "You reached full arm length and shut your eyes, praying the shot wouldn't look tragic."
    )

    // ---------------------------------------------------------
    // 3. MAIN SAVAGE ROAST GENERATOR
    // ---------------------------------------------------------

    fun generateSavageRoast(
        mode: CameraMode,
        pitchErr: Float,
        compassErr: Float,
        zoomErr: Float,
        expressionTitle: String? = null,
        expressionScore: Float = 1.0f,
        peakPlusDistanceCm: Int = 70,
        peakPlusEyesClosed: Boolean = true
    ): String {
        return when {
            mode == CameraMode.PEAK_PLUS -> {
                String.format(Locale.US, PEAK_PLUS_ARM_EYES_ROASTS.random(), peakPlusDistanceCm)
            }
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

    
    val LINKEDIN_PARODY_POSTS: List<String> = listOf(
        // Post #1
        """I spent 12 minutes holding my phone at a precise 37° tilt today.

Here is what intentional sensor friction taught me about B2B enterprise sales:

1. Most people take the easy shot. True leaders align under constraint.
2. Micro-adjustments matter. Being off by 2 degrees is the difference between closing a \$10M ARR contract and getting left on read.
3. The grind never sleeps.

Attached is my Official Certificate of Photographic Conformity. Agree?

#Leadership #B2BSaaS #Mindset #Discipline #TiltShift""".trimIndent(),

        // Post #2
        """Today I failed to hold my smartphone level. I missed the target by 28 degrees.

And it was the best executive coaching session of my career.

In business, we often think our horizon is straight. But without real-time telemetry (OKRs, KPIs, hardware gyroscopes), we drift.

Embrace your crooked angles. The market will roast you, but resilience will build you.

What are you misaligning this quarter?

#GrowthMindset #VulnerabilityInLeadership #FailForward #Disruption""".trimIndent(),

        // Post #3
        """Proud to announce that I have achieved S-Tier Hardware Conformance Certification in Peak Mode.

While others were scrolling reels, I was harmonizing:
- Focal Zoom (3.5x)
- Pitch Angle (45°)
- Compass Azimuth (180° South)

Synergy isn't a buzzword. It's an operational posture.

Big thank you to my mentors, my coffee mug, and the built-in accelerometer for keeping me accountable.

#HighPerformance #Excellence #AuditReady #Synergy #DeepWork""".trimIndent(),

        // Post #4
        """Wake up at 4:30 AM.
Ice bath.
Cold brew.
Align phone camera to exactly 52° tilt while holding plank position.

If you can't control the tilt of your smartphone, you can't control a 50-person product organization.

Stay hungry. Stay tilted.

#Hustle #FoundersJourney #ExecutivePresence #NoExcuses #Discipline""".trimIndent(),

        // Post #5
        """Is AI coming for your camera? No.

Hardware sensors that verbally abuse you when your hands shake are coming for your camera.

Today, an on-device algorithm called my photography 'giving Dutch-angle catastrophe'. It was the most honest performance review I’ve received in 7 years.

Here's my verified audit breakdown below. Thoughts?

#ArtificialIntelligence #PerformanceManagement #AuthenticLeadership #TechTrends""".trimIndent(),

        // Post #6
        """Pivot. Iterate. Re-align.

When TiltShift demanded a 60° angle with 2.0x zoom, my initial reaction was pushback. Legacy thinking.

Instead, I pivoted my wrists 14 degrees clockwise, absorbed the live roast, and executed.

Agile isn't a framework—it's how you hold your phone.

#Agile #ScrumMaster #ProductManagement #ContinuousImprovement #Pivot""".trimIndent(),

        // Post #7
        """Humbled and honored to share that I have been awarded the Certificate of Conformity by TiltShift*.

Never thought a crooked photo of a desk chair could teach me so much about supply chain optimization.

Never stop learning.

#Blessed #Humbled #Gratitude #ProfessionalDevelopment""".trimIndent(),

        // Post #8
        """Unpopular opinion: Perfectly straight photos are a scam propagated by middle management.

True innovators shoot at 73 degrees with magnetic compass interference.

Challenge the orthodoxy. Break the grid.

#Contrarian #Innovation #ThinkingOutsideTheBox #Disruption #Creativity""".trimIndent(),

        // Post #9
        """I asked our VP of Sales why our pipeline was down 14% this quarter.

He showed me a photo taken at a 12° tilt error.

I didn't say a word. He didn't say a word. We both knew.

Sloppy angles lead to sloppy revenue. Period.

#SalesLeadership #CultureOfExcellence #Accountability #AttentionToDetail""".trimIndent(),

        // Post #10
        """Someone told me today: 'Why didn't you just take a normal photo?'

I smiled.

Normal photos get normal results. Normal photos don't require an IMU audit. Normal photos don't push your comfort zone.

Always choose the friction.

#Grit #Resilience #Unapologetic #StandardOverComfort""".trimIndent(),

        // Post #11
        """Today I pitched 47 venture capitalists.

None of them asked about our TAM.
None of them asked about our churn rate.

They only asked one thing: 'Can your founding team hold a smartphone at 45 degrees without trembling?'

I showed them this Certificate of Conformity. Term sheet signed 4 minutes later.

Execution is everything.

#Founders #VentureCapital #Fundraising #StartupLife""".trimIndent(),

        // Post #12
        """I was fired today.

Not by my board. Not by my CEO.

By an on-device camera algorithm that told me my horizon line was 'an insult to geometry'.

Here is why getting roasted by hardware was the turning point of my entrepreneurial journey:

1. Feedback is a gift.
2. Calibration hurts.
3. The truth will set your revenue free.

#CareerGrowth #RadicalCandor #FounderMindset #Resilience""".trimIndent(),

        // Post #13
        """Why I turned down a \$600k Big Tech offer to focus on holding my phone at uncomfortable angles:

Golden handcuffs make your wrists lazy.

When you're comfortable, your pitch angle drifts. You accept 1.0x zoom when the market demands 3.2x.

Comfort is the enemy of enterprise valuation.

#CareerAdvice #HighGrowth #UncomfortableTruths #Ambition""".trimIndent(),

        // Post #14
        """I interviewed 140 senior product managers this week.

Only ONE was able to unlock the shutter on the first attempt.

The rest complained about 'sensor friction'.

We hired the one who leaned into the constraint. That person is now our Chief Alignment Officer.

#Hiring #Recruitment #ProductDesign #TalentAcquisition""".trimIndent(),

        // Post #15
        """A junior engineer came to me crying because TiltShift said their photo had 'earthquake vibes'.

I didn't comfort them.

I handed them a tripod and said: 'In this company, we don't fix feelings. We calibrate IMUs.'

Today, that engineer is a Six Sigma Black Belt in camera stability.

#ToughLove #Mentorship #EngineeringCulture #Excellence""".trimIndent(),

        // Post #16
        """The best pitch deck is not 10 slides on market opportunity.

It is a single cryptographic Certificate of Conformity proving you can align Zoom, Tilt, and Compass simultaneously.

If you can align three sensors under pressure, you can scale to \$50M ARR.

#VCPitch #SeedRound #Startups #Execution""".trimIndent(),

        // Post #17
        """Most managers manage people. True leaders manage gyroscopic tolerances.

When your team drifts by ±5°, do you schedule a 30-minute sync? No. You let the accelerometer roast them.

Automate accountability.

#Management #Automation #Operations #LeadershipStyle""".trimIndent(),

        // Post #18
        """Our company just replaced quarterly performance reviews with a mandatory 60° tilt photo test.

Productivity increased 400% in 48 hours.

Sensors don't have office politics. Sensors only know cold, hard trigonometric truth.

#HRTech #PeopleOperations #PerformanceManagement #Efficiency""".trimIndent(),

        // Post #19
        """What holding a phone at 83° taught me about B2B customer churn:

When you stop paying attention, gravity takes over.

Churn isn't an accident. It's a failure of rotational momentum.

Audit your angles daily.

#CustomerSuccess #ChurnReduction #SaaSMetrics #Retention""".trimIndent(),

        // Post #20
        """Never let anyone tell you that you're 'trying too hard'.

I spent my entire lunch break re-shooting a coffee cup at 2.5x zoom until the algorithm gave me S+ tier approval.

Mediocre people call it obsession. Unicorn founders call it Tuesday.

#Obsession #HighStandards #Relentless #FounderVision""".trimIndent(),

        // Post #21
        """Let's talk about cross-functional rotational alignment.

In Q3, marketing was at 30° pitch, sales was at 45°, and engineering was facing magnetic north.

The result? Total operational dissonance.

Today we unified our stack around a single verified hardware target. Here is the audit trail.

#Synergy #CrossFunctional #CorporateStrategy #Alignment""".trimIndent(),

        // Post #22
        """I ran this photo through our proprietary AI enterprise synergy workflow.

The output was simple: 'Your tilt is unacceptable for a Fortune 500 director.'

I immediately canceled all meetings for the day to calibrate my wrists.

Prioritize what moves the needle.

#Productivity #TimeManagement #ExecutiveStrategy #Focus""".trimIndent(),

        // Post #23
        """Is your agile sprint suffering from sensor drift?

If your team can't ship within ±5° tolerance, your sprint velocity is an illusion.

Story points mean nothing without spatial discipline.

#AgileMethodology #Scrum #SprintPlanning #SoftwareEngineering""".trimIndent(),

        // Post #24
        """Consultants charge \$250k to tell you your organization is misaligned.

TiltShift told me the exact same thing in 3 seconds for free.

Disrupting McKinsey with an accelerometer and savage roasts.

#Consulting #ManagementConsulting #Disruption #Strategy""".trimIndent(),

        // Post #25
        """Circle back. Touch base. Put a pin in it.

Corporate America uses polite euphemisms for 'you are holding your camera crooked'.

I prefer radical algorithmic candor.

If my angle is bad, tell me my angle is bad.

#CorporateCulture #DirectFeedback #RadicalCandor #Communications""".trimIndent(),

        // Post #26
        """A 1% improvement every day means you are 37 times better by next year.

Today my tilt accuracy was 99.4%. Yesterday it was 98.1%.

Compound interest applies to camera angles.

#AtomicHabits #ContinuousImprovement #SelfDevelopment #PersonalGrowth""".trimIndent(),

        // Post #27
        """Stop optimizing your LinkedIn headline. Start optimizing your pitch-to-zoom ratio.

Real executive presence is felt through the tactile vibration of an unlocked shutter button.

Confidence isn't taught. It's measured in degrees.

#ExecutivePresence #PersonalBranding #Authority #Confidence""".trimIndent(),

        // Post #28
        """We audited our executive team's smartphone stability.

Results were alarming: 70% of C-suite officers exhibited wrist wobble under 3.0x magnification.

We have instituted mandatory gyroscopic pilates every morning at 6:00 AM.

#CorporateWellness #ExecutiveHealth #PeakPerformance #WorkplaceCulture""".trimIndent(),

        // Post #29
        """What is the ROI of an intentional 35° camera angle?

- Zero fluff
- High-intent visual telemetry
- S-tier cryptographic audit trail

If you don't understand the ROI, you're not playing the infinite game.

#ROI #InfiniteGame #StrategicThinking #ValueCreation""".trimIndent(),

        // Post #30
        """I don't look at resumes anymore.

I just ask candidates to take a photo of a whiteboard at 15° tilt in Chad Mode.

If they need the spirit level, they lack conviction.

#TalentAcquisition #HiringHacks #Intuition #Leadership""".trimIndent(),

        // Post #31
        """The camera doesn't care about your feelings.
The camera only cares about the dot product of your orientation vectors.

Be like the camera in Q4.

#UnemotionalExecution #DataDriven #ObjectiveTruth #Mindset""".trimIndent(),

        // Post #32
        """Today I cried in the boardroom.

Not because our Series B round fell through.

Because I finally hit 100% sensor conformity after 19 attempts.

Vulnerability is strength. Share your wins.

#Vulnerability #EmotionalIntelligence #Authenticity #Milestones""".trimIndent(),

        // Post #33
        """Don't let anyone who hasn't locked a 45° angle give you business advice.

Theory is cheap. Hardware constraint is where character is forged.

Stay grounded, stay audited.

#Wisdom #RealTalk #ExperienceMatters #Perspective""".trimIndent(),

        // Post #34
        """3 things I learned about scaling a SaaS company while wrestling a compass sensor:

1. Magnetic interference is everywhere (distractions, naysayers, unread Slack messages).
2. Keep your compass true.
3. Hit the shutter before momentum fades.

#SaaS #BusinessLessons #Focus #Momentum""".trimIndent(),

        // Post #35
        """Most people shoot horizontally because society conditioned them to play safe.

Innovators dare to tilt 57 degrees into the unknown.

History remembers the crooked innovators.

#Disruptors #Trailblazers #Innovation #ThinkDifferent""".trimIndent(),

        // Post #36
        """My morning routine:

- 5:00 AM: Cold shower
- 5:15 AM: Journaling 3 things I'm grateful for
- 5:30 AM: Unlocking shutter at 4.2x zoom on first attempt

Win the morning, win the audit.

#MorningRoutine #HighAchiever #HabitsOfSuccess #Discipline""".trimIndent(),

        // Post #37
        """The algorithm roasted me today: 'Steering a pirate ship with that phone?'

At first, my ego flared.

Then I realized: my sales pipeline WAS steering like a pirate ship.

Thank you, algorithm. Correction accepted.

#EgoIsTheEnemy #Humility #SelfAwareness #Growth""".trimIndent(),

        // Post #38
        """Are you building a feature or are you building an angle?

Features can be copied by competitors. A certified 28° sensor posture is intellectual property.

Protect your moat.

#ProductMoats #Defensibility #Strategy #IP""".trimIndent(),

        // Post #39
        """The biggest risk in business is not taking a risk.

The second biggest risk is releasing a photo with 14% pitch discrepancy.

Avoid both.

#RiskManagement #OperationalExcellence #Compliance #Governance""".trimIndent(),

        // Post #40
        """I showed my 6-year-old daughter this Certificate of Conformity.

She said: 'Daddy, why does it say Ragebait Activated?'

I looked into her eyes and said: 'Because in the corporate world, darling, conformity is currency.'

#Parenting #LifeLessons #Legacy #FutureLeaders""".trimIndent(),

        // Post #41
        """Our CEO just mandated Return to Office.

His reason? 'People at home are taking photos with an unverified 8° tilt.'

In-office alignment guarantees sensor integrity.

Can't argue with that data.

#ReturnToOffice #RemoteWork #WorkplaceTrends #OfficeLife""".trimIndent(),

        // Post #42
        """Remote work is great until you realize your home desk has a 3-degree slant.

My productivity collapsed until I shimmed my monitor with copies of Good to Great.

Level your environment before you level your KPIs.

#WorkFromHome #ProductivityHacks #OfficeSetup #Ergonomics""".trimIndent(),

        // Post #43
        """Why asynchronous communication fails without gyroscopic telemetry:

When you can't see your teammates' wrist angles, trust erodes.

Attach a Certificate of Conformity to every Pull Request.

#DevOps #AsyncWork #EngineeringProductivity #Culture""".trimIndent(),

        // Post #44
        """I had a 1-on-1 with my direct report today.

We didn't discuss deliverables.

We simply reviewed their sensor audit history for the past 14 days.

Zero words spoken. Total alignment reached.

#OneOnOnes #PeopleManagement #RadicalSimplicity #Leadership""".trimIndent(),

        // Post #45
        """Burnout is real.

If you find yourself shaking at 1.5x zoom, take a mental health walk.

Calibrate your spirit before you calibrate your shutter.

#MentalHealth #Wellness #SelfCare #WorkLifeBalance""".trimIndent(),

        // Post #46
        """To all the founders grinding this weekend:

Your valuation is not your self-worth.

Your self-worth is determined strictly by your accuracy percentage on a 45° shot.

Keep pushing.

#FounderMentalHealth #WeekendGrind #Perserverance #Community""".trimIndent(),

        // Post #47
        """I asked ChatGPT to write a poem about my camera accuracy score.

It wrote:
'The wrist was steady, the sensor smiled,
A corporate titan, reconciled.'

AI gets it.

#GenAI #Poetry #TechHumor #Milestone""".trimIndent(),

        // Post #48
        """Stop having 60-minute meetings that could have been an 8-second sensor lock.

Streamline your communication pipeline.

#MeetingCulture #Efficiency #ProductivityMatters #TimeOptimization""".trimIndent(),

        // Post #49
        """What does high-performance culture look like?

It looks like 12 engineers silently holding their phones at 30 degrees until every shutter button clicks in unison.

True synchronization.

#Culture #Teamwork #HighPerformance #Execution""".trimIndent(),

        // Post #50
        """I took a photo today that scored 62% accuracy.

I immediately stepped down as CEO and appointed my executive assistant.

Integrity requires knowing when your hands aren't steady enough to lead.

#Accountability #Integrity #CorporateGovernance #Succession""".trimIndent(),

        // Post #51
        """Friction is where value is created.

When a camera lets anyone snap a photo effortlessly, photography has zero margin.

When a camera forces you into physical gymnastics before clicking, you have created scarcity.

Create scarcity in your business.

#EconomicTheory #PricingPower #Scarcity #Moats""".trimIndent(),

        // Post #52
        """The camera that insults you is the camera that cares about you.

Polite software allows you to stagnate. Savage software pushes you into the Fortune 500.

Demand tougher tools.

#ProductPhilosophy #SoftwareDesign #UXDesign #ToughLove""".trimIndent(),

        // Post #53
        """Don't outsource your horizon to an optical image stabilizer.

Take personal responsibility for your pitch axis.

Sovereignty begins with your own wrists.

#PersonalResponsibility #OwnershipMindset #ExtremeOwnership #Sovereignty""".trimIndent(),

        // Post #54
        """If your goals don't scare you, you're not aiming high enough.

If your camera angle doesn't make your forearm cramp, you're not tilting hard enough.

Stretch your limits.

#BigGoals #Limitless #ForearmStrength #Ambition""".trimIndent(),

        // Post #55
        """A mentor once told me: 'Show me your camera roll, and I will show you your financial future.'

Today my roll consists exclusively of cryptographically verified tilt audits.

My future has never looked more conformal.

#Mentorship #FutureProof #WealthBuilding #Clarity""".trimIndent(),

        // Post #56
        """We live in an era of shortcuts.

Filters, presets, auto-levels.

TiltShift brings back the artisanal beauty of sweating for 4 minutes to match a 65° target.

Craftsmanship lives here.

#Craftsmanship #ArtisanalSoftware #Authenticity #SlowTech""".trimIndent(),

        // Post #57
        """Every time you hit shutter without matching the angle, an angel loses its Series A funding.

Respect the protocol.

#Protocols #Standards #Compliance #Discipline""".trimIndent(),

        // Post #58
        """I used to think empathy was the most important leadership trait.

Then I realized: empathy without hardware precision is just hand-waving.

Lead with metrics.

#LeadershipDevelopment #MetricsMatter #DataDrivenLeadership #Execution""".trimIndent(),

        // Post #59
        """Why my startup is pivoting to Gyro-Centric Commerce:

Users who can't align their sensors shouldn't have access to high-ticket checkout flows.

Gatekeeping builds brand prestige.

#ECommerce #BrandBuilding #LuxuryStrategy #Innovation""".trimIndent(),

        // Post #60
        """A cold email has a 2% reply rate.

A cold email with an attached TiltShift Certificate of Conformity has a 94% reply rate.

Prospects respect proof of physical dedication.

#SalesHacks #OutboundSales #Prospecting #ColdEmail""".trimIndent(),

        // Post #61
        """The cloud is just someone else's computer.

Your pitch angle is yours and yours alone.

Reclaim on-device computing.

#EdgeAI #CloudComputing #Hardware #Cybersecurity""".trimIndent(),

        // Post #62
        """We just raised \$12M on a SAFENOTE to bring blockchain verification to tilted photos.

Proof of Work is dead. Proof of Tilt is the future of trust.

Web3 meets spatial reality.

#Web3 #Blockchain #Crypto #ProofOfTilt""".trimIndent(),

        // Post #63
        """They said Steve Jobs would never have approved a camera that yells at you.

Wrong. Steve would have made the insults louder.

Pushing humans toward perfection was his whole ethos.

#SteveJobs #AppleLegacy #DesignExcellence #Perfectionism""".trimIndent(),

        // Post #64
        """I showed my pitch deck to a tier-1 VC in Menlo Park.

He opened the camera, pointed it at his matcha latte, and missed the angle by 18°.

The app called him a 'horizontal amateur'.

He invested \$5M on the spot. 'I haven't been spoken to like that since Harvard Business School.'

#VCLife #SiliconValley #FundraisingStories #Matcha""".trimIndent(),

        // Post #65
        """Why generative AI will never replace human photographers:

Midjourney doesn't know the physical agony of holding 5.0x zoom while your dog runs across the room.

Human struggle cannot be synthesized.

#HumanInTheLoop #GenerativeAI #Creativity #TheStruggle""".trimIndent(),

        // Post #66
        """The future of SaaS pricing is dynamic tolerance:

- Free Tier: ±15° tolerance
- Pro Tier: ±5° tolerance
- Enterprise Tier: ±0.5° tolerance with a dedicated Customer Success Gyroscope

Monetize precision.

#PricingStrategy #SaaSPricing #ProductGrowth #PLG""".trimIndent(),

        // Post #67
        """I audited our churned enterprise accounts.

100% of churned logos were using default camera apps.

Correlation is causation. You heard it here first.

#DataAnalytics #CustomerRetention #Analytics #Insights""".trimIndent(),

        // Post #68
        """Don't pitch me your roadmap.

Pitch me your pitch angle.

If the angle isn't sharp, the roadmap isn't real.

#Roadmap #ProductStrategy #Vision #Clarity""".trimIndent(),

        // Post #69
        """My board asked me what our defensive moat was.

I handed them my phone locked at 42° tilt with 3.5x zoom and said: 'Try to replicate this.'

Meeting adjourned early.

#BoardMeeting #CompetitiveAdvantage #Moats #Defense""".trimIndent(),

        // Post #70
        """To the recruiter who asked for 10 years of experience with an API released 3 weeks ago:

Here is my Certificate of Sensor Conformity instead.

Adaptability beats tenure every time.

#RecruitingFails #TechJobs #Adaptability #Hiring""".trimIndent(),

        // Post #71
        """If you want to be in the top 1%, you must do what the 99% won't do.

The 99% take photos standing up straight.

The top 1% crouch like a mantis to hit a 15° floor-level target.

Embrace the mantis.

#TopOnePercent #SuccessMindset #Unconventional #Winning""".trimIndent(),

        // Post #72
        """5 things holding your career back:

1. Complacency
2. Lack of networking
3. Fear of public speaking
4. Wrist tremble during 2.5x magnification
5. Ignoring compass deviation

Fix #4 and #5 first.

#CareerTips #ProfessionalGrowth #SelfImprovement #Advice""".trimIndent(),

        // Post #73
        """Never let a camera tell you what you can't do.

Except TiltShift. If TiltShift says you are off by 12°, you are off by 12°. Respect the mathematics.

Math is non-negotiable.

#Truth #Mathematics #Humility #Acceptance""".trimIndent(),

        // Post #74
        """I asked our summer intern why they were holding their phone upside down.

They replied: 'Disrupting the vertical paradigm.'

Promoted to Senior VP of Disruption with immediate stock options.

#InternStories #NextGen #YouthInTech #Disruption""".trimIndent(),

        // Post #75
        """What is the difference between a Junior Photographer and a Principal Architect?

A Junior blames the lighting.
A Principal Architect re-calibrates their IMU and absorbs the roast with stoic serenity.

Level up your seniority.

#Seniority #EngineeringMaturity #Stoicism #Mindset""".trimIndent(),

        // Post #76
        """I lost a \$2M client today because my photo was 4° too level.

They said: 'If you can't lean into the tilt, you can't lean into our market expansion.'

Painful lesson, but necessary.

#Losses #HardLessons #BusinessReality #Growth""".trimIndent(),

        // Post #77
        """The best networking event isn't a conference in Vegas.

It's two executives in the airport lounge, both silently sweating while trying to unlock a 70° camera shot.

Instant unspoken bond.

#Networking #ExecutiveBonds #Airports #TravelLife""".trimIndent(),

        // Post #78
        """Why I don't use filters:

Filters are cosmetic.
Sensor conformity is structural.

Build things that are structurally sound from the inside out.

#AuthenticLiving #NoFilters #RealTalk #BuildingValue""".trimIndent(),

        // Post #79
        """If your photography isn't getting roasted by an algorithm, are you even pushing creative boundaries?

Praise is comfortable. Roasts build empires.

Welcome the critique.

#Critique #CreativeProcess #GrowthMindset #Boldness""".trimIndent(),

        // Post #80
        """I had an epiphany during my 10-day silent meditation retreat:

Inner peace is just an angle locked within ±5° tolerance.

Everything else is noise.

#Mindfulness #Meditation #InnerPeace #Clarity""".trimIndent(),

        // Post #81
        """They laughed when I pulled out my phone.

They went silent when the green S-tier border illuminated.

Competence speaks louder than words.

#Confidence #ProofOfWork #Results #Mastery""".trimIndent(),

        // Post #82
        """A quarterly business review without certified photo proof is just creative writing.

Back your metrics with hardware telemetry.

#QBR #ExecutiveReporting #Transparency #DataIntegrity""".trimIndent(),

        // Post #83
        """How I manage stress as a tech executive:

When a crisis hits, I don't panic.

I open TiltShift, set difficulty to Hell, and spend 15 minutes aligning a 85° shot.

If you can steady your hands under pressure, you can navigate any market correction.

#StressManagement #ExecutiveResilience #GraceUnderPressure #Leadership""".trimIndent(),

        // Post #84
        """Stop telling people what you're going to do.

Show them your cryptographically signed Certificate of Conformity.

Audit trails don't lie.

#ShowDontTell #ExecutionOverTalk #Accountability #Action""".trimIndent(),

        // Post #85
        """Our company's 4 core values:

1. Radical Transparency
2. Customer Obsession
3. Compass Heading Accuracy
4. Speed of Shutter Engagement

Live your values every day.

#CompanyCulture #CoreValues #LivingTheBrand #Purpose""".trimIndent(),

        // Post #86
        """Someone commented on my last post: 'This is just a gimmick.'

They said the same thing about the iPhone in 2007.
They said the same thing about the wheel in 3500 BC.

First they ignore you, then they roast you, then you achieve S-Tier conformity.

#Visionary #FirstPrinciples #Pioneering #Belief""".trimIndent(),

        // Post #87
        """Leadership is not a title. It's a spatial posture.

When you enter the boardroom, is your posture locked at 0° or are you drifting?

Hold your ground.

#ExecutivePresence #BodyLanguage #Presence #LeadershipIdentity""".trimIndent(),

        // Post #88
        """The easiest way to spot an amateur founder:

They complain when the camera target is hard to reach.

True founders bend their knees, contort their spine, and lock the shot.

Flexibility is non-optional in hypergrowth.

#Hypergrowth #Adaptability #FoundersGrit #Relentless""".trimIndent(),

        // Post #89
        """Why we implemented a Zero-Shake Policy across all departments:

Wobbly hands produce wobbly spreadsheets.

Eliminate variance at the physical layer.

#OperationalDiscipline #ZeroDefects #QualityAssurance #SixSigma""".trimIndent(),

        // Post #90
        """I asked our AI assistant: 'How do I become a Fortune 500 CEO?'

It replied: 'Learn to photograph a coffee cup at 45 degrees while an algorithm makes fun of your lineage.'

Sound advice. Already halfway there.

#CareerProgression #AIInsights #Ambition #ThePath""".trimIndent(),

        // Post #91
        """Agree or Disagree?

'A crooked photo taken with intention is worth 10,000 straight photos taken with apathy.'

Drop your thoughts in the comments below. Let's start a conversation.

#AgreeOrDisagree #EngagementHacks #ThoughtOfTheDay #Debate""".trimIndent(),

        // Post #92
        """I had 5 minutes before my keynote presentation at Dreamforce.

Instead of rehearsing my slides, I unlocked this Peak Mode target.

The energy in that room was electric. They could sense the sensor discipline radiating from the stage.

Keynotes are won backstage.

#PublicSpeaking #Dreamforce #Energy #Preparation""".trimIndent(),

        // Post #93
        """Don't chase followers. Chase gyroscopic conformity.

Followers are vanity. S-Tier audit scores are sanity.

Prioritize substance over vanity metrics.

#VanityMetrics #Substance #RealResults #FocusOnWhatMatters""".trimIndent(),

        // Post #94
        """The most dangerous phrase in business: 'We've always held the phone this way.'

Challenge the default.
Break your wrist angle. Re-imagine the capture pipeline.

#ChangeManagement #Disruption #ModernLeadership #Evolution""".trimIndent(),

        // Post #95
        """I bought a coffee today. The barista's hands were trembling at 1.2x zoom.

I tipped them 100% and said: 'We've all been there. Keep practicing your sensor lock.'

Kindness costs nothing. Sensor calibration costs sweat.

#Kindness #Empathy #HumanityFirst #Encouragement""".trimIndent(),

        // Post #96
        """What holding a smartphone at 60° taught me about cross-border M&A:

Due diligence is tedious.
Alignment feels impossible.
The roast is intense.

But when the lock clicks, the deal is inked.

#MergersAndAcquisitions #DealMaking #Finance #CorporateFinance""".trimIndent(),

        // Post #97
        """If your camera app doesn't make you want to throw your phone across the room, it's not testing your emotional regulation.

Emotional regulation is the #1 predictor of C-suite success.

Thank you TiltShift for the daily therapy session.

#EmotionalIntelligence #EQ #ResilienceTraining #PersonalGrowth""".trimIndent(),

        // Post #98
        """Stop looking for a mentor who will tell you what you want to hear.

Find an algorithm that will tell you: 'Off by 35°! Are you trying to photograph the ceiling or floor?!'

Unfiltered truth is the fastest elevator to greatness.

#Mentorship #UnfilteredTruth #Accelerate #PeakPerformance""".trimIndent(),

        // Post #99
        """I told my investor I was spending company resources on sensor conformity.

He pulled out his phone, showed me his own TiltShift Certificate with 99.8% accuracy, and wired an additional \$2M.

Winners recognize winners.

#Synergy #InvestorRelations #WinningCulture #Alpha""".trimIndent(),

        // Post #100
        """This is not just a photograph.

This is proof of human triumph over gyroscopic entropy.

In a world of noise, we found the angle.

Certificate of Conformity attached. Read it and lead.

#Triumph #Conformity #SpatialDiscipline #Leadership #TiltShift""".trimIndent()
    )

    fun selectBestLinkedInPost(
        accuracy: Float,
        grade: String,
        mode: com.example.simplecamera.ui.camera.CameraMode,
        pitchErr: Float
    ): String {
        return when {
            accuracy >= 98f -> LINKEDIN_PARODY_POSTS[2] // Peak Mode Perfection
            pitchErr > 12f -> LINKEDIN_PARODY_POSTS[1]  // Crooked Failure Reflection
            accuracy >= 90f -> LINKEDIN_PARODY_POSTS[0] // High Accuracy Chad
            else -> LINKEDIN_PARODY_POSTS.random()
        }
    }
}
