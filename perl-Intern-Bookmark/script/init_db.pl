#!/usr/bin/env perl
use strict;
use warnings;
use utf8;

use Encode qw(encode_utf8 decode_utf8);
use Pod::Usage;

use FindBin;
use lib "$FindBin::Bin/../lib";

use DBIx::Sunny;

use Intern::Bookmark::Config;

my $db      = do {
    my $config = config->param('db')->{intern_bookmark};
    DBIx::Sunny->connect(map { $config->{$_} } qw(dsn user password));
};

create_schema();

exit 0;

sub create_schema {
    my $schema = do {
        open my $fh, '<', './db/schema.sql' or die;
        local $/;
        <$fh>
    };
    chomp $schema;
    for my $stmt (split(/;/, $schema)) {
        print $stmt;
        $db->do($stmt);
    }
}

__END__

=head1 NAME

init_db.pl - create tables

=head1 SYNOPSIS

  init_db.pl

=cut
