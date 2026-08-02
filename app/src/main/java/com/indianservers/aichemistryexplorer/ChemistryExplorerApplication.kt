package com.indianservers.aichemistryexplorer

import android.app.Application
import com.indianservers.aichemistryexplorer.contentpack.ChemistryMcqContent
import com.indianservers.aichemistryexplorer.contentpack.ContentPackRepository
import com.indianservers.aichemistryexplorer.contentpack.ContentPackServiceOwner
import com.indianservers.aichemistryexplorer.contentpack.ContentPackServices

class ChemistryExplorerApplication : Application(), ContentPackServiceOwner {
    override val contentPackServices: ContentPackServices by lazy {
        ContentPackServices(this, ChemistryMcqContent.config)
    }

    internal val chemistryMcqRepository: ContentPackRepository by lazy {
        ContentPackRepository(this, ChemistryMcqContent.config, contentPackServices)
    }
}
