/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.core.network;

import io.bisq.network.p2p.NodeAddress;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class NodeAddresses extends ImmutableSetDecorator<NodeAddress> {
    static NodeAddresses fromString(String seedNodes) {
        if (seedNodes.isEmpty()) {
            return new NodeAddresses(Collections.emptySet());
        }

        String trimmed = StringUtils.deleteWhitespace(seedNodes);
        String[] nodes = trimmed.split(",");
        return Arrays.stream(nodes)
                .map(NodeAddress::new)
                .collect(collector());
    }

    NodeAddresses(Set<NodeAddress> delegate) {
        super(delegate);
    }

    NodeAddresses excludeByHost(Set<String> hosts) {
        Set<NodeAddress> copy = new HashSet<>(this);
        copy.removeIf(address -> {
            String hostName = address.getHostName();
            return hosts.contains(hostName);
        });
        return new NodeAddresses(copy);
    }

    NodeAddresses excludeByFullAddress(String fullAddress) {
        Set<NodeAddress> copy = new HashSet<>(this);
        copy.removeIf(address -> fullAddress.equals(address.getFullAddress()));
        return new NodeAddresses(copy);
    }

    static Collector<NodeAddress, ?, NodeAddresses> collector() {
        return Collectors.collectingAndThen(Collectors.toSet(), NodeAddresses::new);
    }
}
