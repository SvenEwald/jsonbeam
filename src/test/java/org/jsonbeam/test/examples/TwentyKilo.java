package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface TwentyKilo {

	@JBExpect(strings = { "548 Maple Avenue, Russellville, Pennsylvania, 2467", "355 Brighton Avenue, Shrewsbury, Washington, 8719", "688 Wyckoff Street, Carrizo, California, 6999", "880 Pilling Street, Omar, Minnesota, 8563", "637 Colby Court, Whitewater, Vermont, 1992",
			"503 Linden Street, Jackpot, Rhode Island, 419", "947 Sumner Place, Haena, North Dakota, 685", "902 Elmwood Avenue, Caberfae, Delaware, 4651", "133 Norfolk Street, Worcester, Virginia, 3631", "966 Winthrop Street, Weogufka, Massachusetts, 2510", "976 Lexington Avenue, Jacumba, Tennessee, 2110",
			"112 Montieth Street, Hegins, Arkansas, 614", "674 Gerritsen Avenue, Needmore, Michigan, 608", "174 Herkimer Court, Hamilton, Indiana, 8465", "166 Chauncey Street, Coultervillle, Idaho, 8121", "117 Chase Court, Joes, Alabama, 774", "248 Elliott Place, Walland, New Jersey, 8005",
			"773 Brighton Court, Kenwood, Missouri, 6928", "216 Fairview Place, Balm, Louisiana, 5648", "646 Harwood Place, Bodega, New Hampshire, 606", "461 Conover Street, Epworth, Wyoming, 2303", "691 Putnam Avenue, Nettie, Arizona, 7175" })
	@JBRead("..address")
	List<String> getAdresses();
}
