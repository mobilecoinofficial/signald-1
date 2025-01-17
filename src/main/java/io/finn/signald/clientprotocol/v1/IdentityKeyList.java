/*
 * Copyright (C) 2021 Finn Herzfeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.finn.signald.clientprotocol.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.finn.signald.annotations.Doc;
import io.finn.signald.db.IdentityKeysTable;
import io.finn.signald.util.SafetyNumberHelper;
import org.whispersystems.libsignal.fingerprint.Fingerprint;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;

import java.util.ArrayList;
import java.util.List;

@Doc("a list of identity keys associated with a particular address")
public class IdentityKeyList {
  @JsonIgnore private final SignalServiceAddress ownAddress;
  @JsonIgnore private final org.whispersystems.libsignal.IdentityKey ownKey;

  public final JsonAddress address;
  public final List<IdentityKey> identities = new ArrayList<>();

  public IdentityKeyList(SignalServiceAddress ownAddress, org.whispersystems.libsignal.IdentityKey ownKey, SignalServiceAddress a,
                         List<IdentityKeysTable.IdentityKeyRow> identities) {
    this.ownAddress = ownAddress;
    this.ownKey = ownKey;
    this.address = new JsonAddress(a);
    if (identities == null) {
      return;
    }
    for (IdentityKeysTable.IdentityKeyRow i : identities) {
      addKey(i);
    }
  }

  public void addKey(IdentityKeysTable.IdentityKeyRow identity) {
    Fingerprint safetyNumber = SafetyNumberHelper.computeFingerprint(ownAddress, ownKey, address.getSignalServiceAddress(), identity.getKey());
    if (safetyNumber == null) {
      return;
    }
    identities.add(new IdentityKey(identity, safetyNumber));
  }
}
