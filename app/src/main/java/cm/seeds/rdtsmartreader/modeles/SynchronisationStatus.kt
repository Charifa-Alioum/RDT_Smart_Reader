package cm.seeds.rdtsmartreader.modeles

data class SynchronisationStatus(
        var userSynched : Long = 0,
        var userToSynch : Long = 0,
        var synchronisationFinished : Boolean = false
) {
}