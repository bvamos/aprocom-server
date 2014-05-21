db.adminCommand({ create: 'helyseg',
	  capped: false,
	  autoIndexId: true,
	  flags: 0
	});

db.adminCommand({ create: 'hirdetes',
	  capped: false,
	  autoIndexId: true,
	  flags: 1
	});

db.adminCommand({ create: 'hirdeto',
	  capped: false,
	  autoIndexId: true,
	  flags: 1
	});

db.adminCommand({ create: 'kategoria',
	  capped: false,
	  autoIndexId: true,
	  flags: 0
	});

db.adminCommand({ create: 'kep',
	  capped: false,
	  autoIndexId: true,
	  flags: 1
	});

db.adminCommand({ create: 'session',
	  capped: false,
	  autoIndexId: true,
	  flags: 1
	});
