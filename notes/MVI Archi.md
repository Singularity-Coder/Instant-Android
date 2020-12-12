# MVI ARCHI NOTES

MVI stands for Model View Intent. Unidirectional data flow, Immutable state. MVI components - Intents, action, result, state. MVI inherently uses reactive programming paradigm while all other architectures which we saw in the previous tutorials use imperative programming paradigm. Like in MVP, Interfaces in MVI represent Views. Cyclical flow.
Intent represents an intention or a desire to perform an action, either by the user or the app itself. For every action, a View receives an Intent. The Presenter observes the Intent, and Models translate it into a new state.
M - Model: Holds both data and the state unlike other architectures which only hold data. State is the current position of the app after a change. A change can be a button click, a different view, etc. It,s what the user sees at any given moment. V - View I - Intent
