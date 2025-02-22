/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <libinit_dalvik_heap.h>
#include <libinit_lmk.h>
#include <libinit_utils.h>

#include "vendor_init.h"

void vendor_load_properties() {
    set_dalvik_heap();
    set_lmk();

    // SafetyNet workaround
    property_override("ro.boot.verifiedbootstate", "green");
    property_override("ro.oem_unlock_supported", "0");
}
