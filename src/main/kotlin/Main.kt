package me.dzikimlecz.coffeepot
import tornadofx.launch
import gui.MainView
import tornadofx.App

fun main(args: Array<String>) = launch<CoffeePot>(args)


class CoffeePot : App(MainView::class)