/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 *
 * @providesModule HijriDatePickerAndroid
 * @flow
 */
'use strict';

const HijriDatePickerAndroid = {
  async open(options: Object): Promise<Object> {
    return Promise.reject({
      message: 'HijriDatePickerAndroid is not supported on this platform.'
    });
  },
};

module.exports = HijriDatePickerAndroid;
