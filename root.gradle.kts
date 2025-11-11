plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.21.10-fabric"(1_21_10, "srg") {
        "1.21.8-fabric"(1_21_08, "srg") {
            "1.21.5-fabric"(1_21_05, "srg") {
                "1.21.4-fabric"(1_21_04, "srg") {
                    "1.21.1-fabric"(1_21_01, "srg") {
                        "1.16.5-fabric"(1_16_05, "srg", file("versions/mappings/1.21.1-fabric+1.16.5-fabric.txt")) {
                            "1.16.5-forge"(1_16_05, "srg") {
                                "1.12.2-forge"(1_12_02, "srg") {
                                    "1.12.2-fabric"(1_12_02, "srg") {
                                        "1.8.9-fabric"(1_08_09, "srg") {
                                            "1.8.9-forge"(1_08_09, "srg")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    strictExtraMappings.set(true)
}